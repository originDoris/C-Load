package com.cl.framework.core.utils.reflection;


import com.cl.framework.core.annotation.Contract;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.web.core.Pool;
import com.esotericsoftware.reflectasm.MethodAccess;
import io.vertx.core.impl.logging.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 替代{@link Reflect} 工具
 */
public abstract class ReflectionUtils {

    private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";

    private static final Method[] NO_METHODS = {};

    private static final Field[] NO_FIELDS = {};

    public static final ConcurrentMap<String, Object> SINGLETON = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Method[]> declaredMethodsCache =
            new ConcurrentReferenceHashMap<Class<?>, Method[]>(256);

    private static final Map<Class<?>, Field[]> declaredFieldsCache =
            new ConcurrentReferenceHashMap<Class<?>, Field[]>(256);

    private static final Map<Class<?>, Constructor[]> declaredConstructorCache =
            new ConcurrentReferenceHashMap<Class<?>, Constructor[]>(256);

    public static <T> T invoke(Object target, String methodName) {
        Method method = findMethod(target.getClass(), methodName);
        return invokeMethod(method, target);
    }

    public static <T> T invoke(Object target, String methodName, Object... args) {
        return invokeObject(target, methodName, args);
    }

    public static <T> T invokeObject(
            final Object instance,
            final String name,
            final Object... args) {
        return CubeFn.getDefault(null,() -> {
            final MethodAccess access = MethodAccess.get(instance.getClass());
            Object result;
            try {
                result = access.invoke(instance, name, args);
            } catch (final Throwable ex) {
                ex.printStackTrace();
                final int index;
                final List<Class<?>> types = new ArrayList<>();
                for (final Object arg : args) {
                    types.add(FieldUtil.toPrimary(arg.getClass()));
                }
                index = access.getIndex(name, types.toArray(new Class<?>[]{}));
                result = access.invoke(instance, index, args);
            }
            final Object ret = result;
            return CubeFn.getDefault(null, () -> (T) ret, ret);
        }, instance, name);
    }


    public static <T> T invokeStatic(Class clazz, String methodName, Object... args) {
        Class[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i] == null ? NULL.class : args[i].getClass();
        }
        Method method = findMethod(clazz, methodName, paramTypes);
        return (T) invokeMethod(method, null, args);
    }

    public static <T> T get(Object target, String fieldName) {
        Field field = findField(target.getClass(), fieldName);
        return (T) getField(field, target);
    }

    public static void set(Object target, String fieldName, Object value) {
        Field field = findField(target.getClass(), fieldName);
        setField(field, target, value);
    }

    public static <T> T newInstance(String className, Object... args) {
        Class[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                paramTypes[i] = NULL.class;
            } else if(args[i] instanceof Class) {
                paramTypes[i] = (Class) args[i];
            }else{
                paramTypes[i] = args[i].getClass();
            }
        }
        try {
            Class<?> clazz = Class.forName(className);
            Constructor[] constructors = getDeclaredConstructors(clazz);
            for (Constructor constructor : constructors) {
                Class[] types = constructor.getParameterTypes();
                if (types.length != paramTypes.length) {
                    continue;
                }
                boolean match = true;
                for (int i = 0; i < types.length; i++) {
                    Class t1 = paramTypes[i];
                    if (t1 == NULL.class) {
                        continue;
                    }
                    if (!types[i].isAssignableFrom(t1)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return (T) constructor.newInstance(args);
                }
            }
            throw new IllegalStateException("can`t find match constructor for class :" + className);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) {
        return newInstance(clazz.getName(), args);
    }

    public static <T> T getFieldValues(Object object, String... filedNames) {
        for (String filedName : filedNames) {
            object = get(object, filedName);
        }
        return (T) object;
    }

    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    public static Field findField(Object obj, String name) {
        Assert.notNull(obj, "Target must not be null");
        return findField(obj.getClass(), name, null);
    }

    public static boolean existsField(Class<?> clazz, String name) {
        return findField(clazz, name) != null;
    }

    public static boolean existsField(Object target, String name) {
        Assert.notNull(target, "Target must not be null");
        return findField(target.getClass(), name) != null;
    }

    public static <T> T getStatic(Class<?> clazz, String name) {
        Field field = findField(clazz, name, null);
        try {
            return (T) field.get(null);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = getDeclaredFields(searchType);
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) &&
                        (type == null || type.equals(field.getType()))) {
                    field.setAccessible(true);
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static void setField(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    public static <T> T getField(Field field, Object target) {
        try {
            return (T) field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    public static boolean existsMethod(Class<?> clazz, String methodName) {
        return existsMethod(clazz, methodName, new Object[0]);
    }

    public static boolean existsMethod(Class<?> clazz, String methodName, Object... args) {
        Class[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i] == null ? NULL.class : args[i].getClass();
        }
        return findMethod(clazz, methodName, paramTypes) != null;
    }


    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, new Class<?>[0]);
    }


    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
            for (Method method : methods) {
                if (name.equals(method.getName()) &&
                        (paramTypes == null || classesEquals(paramTypes, method.getParameterTypes()))) {
                    method.setAccessible(true);
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static <T> T invokeMethod(Method method, Object target) {
        return (T) invokeMethod(method, target, new Object[0]);
    }

    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    public static <T> T get(Object target, String fieldName, Class<T> type) {
        Field field = findField(target.getClass(), fieldName, type);
        return (T) getField(field, target);
    }

    public static Object invokeJdbcMethod(Method method, Object target) throws SQLException {
        return invokeJdbcMethod(method, target, new Object[0]);
    }

    public static Object invokeJdbcMethod(Method method, Object target, Object... args) throws SQLException {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof SQLException) {
                throw (SQLException) ex.getTargetException();
            }
            handleInvocationTargetException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static boolean isCglibRenamedMethod(Method renamedMethod) {
        String name = renamedMethod.getName();
        if (name.startsWith(CGLIB_RENAMED_METHOD_PREFIX)) {
            int i = name.length() - 1;
            while (i >= 0 && Character.isDigit(name.charAt(i))) {
                i--;
            }
            return ((i > CGLIB_RENAMED_METHOD_PREFIX.length()) &&
                    (i < name.length() - 1) && name.charAt(i) == '$');
        }
        return false;
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    public static void doWithLocalMethods(Class<?> clazz, MethodCallback mc) {
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            try {
                mc.doWith(method);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
    }

    public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
        doWithMethods(clazz, mc, null);
    }

    public static void doWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf) {
        // Keep backing up the inheritance hierarchy.
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            if (mf != null && !mf.matches(method)) {
                continue;
            }
            try {
                mc.doWith(method);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
        if (clazz.getSuperclass() != null) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        } else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }

    public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
        final List<Method> methods = new ArrayList<Method>(32);
        doWithMethods(leafClass, new MethodCallback() {
            @Override
            public void doWith(Method method) {
                methods.add(method);
            }
        });
        return methods.toArray(new Method[methods.size()]);
    }


    public static boolean noarg(final Class<?> clazz) {
        Constructor[] declaredConstructors = getDeclaredConstructors(clazz);
        boolean noarg = false;
        for (final Constructor<?> constructor : declaredConstructors) {
            if (0 == constructor.getParameterTypes().length) {
                noarg = true;
            }
        }
        return noarg;
    }

    public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
        final List<Method> methods = new ArrayList<Method>(32);
        doWithMethods(leafClass, new MethodCallback() {
            @Override
            public void doWith(Method method) {
                boolean knownSignature = false;
                Method methodBeingOverriddenWithCovariantReturnType = null;
                for (Method existingMethod : methods) {
                    if (method.getName().equals(existingMethod.getName()) && classesEquals(method.getParameterTypes(), existingMethod.getParameterTypes())) {
                        // Is this a covariant return type situation?
                        if (existingMethod.getReturnType() != method.getReturnType() &&
                                existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
                            methodBeingOverriddenWithCovariantReturnType = existingMethod;
                        } else {
                            knownSignature = true;
                        }
                        break;
                    }
                }
                if (methodBeingOverriddenWithCovariantReturnType != null) {
                    methods.remove(methodBeingOverriddenWithCovariantReturnType);
                }
                if (!knownSignature && !isCglibRenamedMethod(method)) {
                    methods.add(method);
                }
            }
        });
        return methods.toArray(new Method[methods.size()]);
    }

    private static Method[] getDeclaredMethods(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Method[] result = declaredMethodsCache.get(clazz);
        if (result == null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
            if (defaultMethods != null) {
                result = new Method[declaredMethods.length + defaultMethods.size()];
                System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                int index = declaredMethods.length;
                for (Method defaultMethod : defaultMethods) {
                    result[index] = defaultMethod;
                    index++;
                }
            } else {
                result = declaredMethods;
            }
            declaredMethodsCache.put(clazz, (result.length == 0 ? NO_METHODS : result));
        }
        return result;
    }

    private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new ArrayList<Method>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

    public static void doWithLocalFields(Class<?> clazz, FieldCallback fc) {
        for (Field field : getDeclaredFields(clazz)) {
            try {
                fc.doWith(field);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
            }
        }
    }

    public static List<Field> getAllDeclaredFields(Class<?> leafClass) {
        final List<Field> fields = new ArrayList<Field>(32);
        doWithFields(leafClass, new FieldCallback() {
            @Override
            public void doWith(Field field) {
                fields.add(field);
            }
        });
        return fields;
    }


    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        // 尝试在当前类中查找字段
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // 如果当前类中没有找到字段，继续在父类中查找
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found");
    }


    public static void doWithFields(Class<?> clazz, FieldCallback fc) {
        doWithFields(clazz, fc, null);
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc, FieldFilter ff) {
        // Keep backing up the inheritance hierarchy.
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                if (ff != null && !ff.matches(field)) {
                    continue;
                }
                try {
                    fc.doWith(field);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null) {
            result = clazz.getDeclaredFields();
            declaredFieldsCache.put(clazz, (result.length == 0 ? NO_FIELDS : result));
        }
        return result;
    }

    private static Constructor[] getDeclaredConstructors(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Constructor[] result = declaredConstructorCache.get(clazz);
        if (result == null) {
            result = clazz.getDeclaredConstructors();
            declaredConstructorCache.put(clazz, result);
        }
        return result;
    }

    public static void clearCache() {
        declaredMethodsCache.clear();
        declaredFieldsCache.clear();
    }

    public interface MethodCallback {

        void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
    }

    public interface MethodFilter {

        boolean matches(Method method);
    }

    public interface FieldCallback {

        void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
    }

    public interface FieldFilter {

        boolean matches(Field field);
    }

    public static final MethodFilter NON_BRIDGED_METHODS = new MethodFilter() {

        @Override
        public boolean matches(Method method) {
            return !method.isBridge();
        }
    };

    public static final MethodFilter USER_DECLARED_METHODS = new MethodFilter() {

        @Override
        public boolean matches(Method method) {
            return (!method.isBridge() && method.getDeclaringClass() != Object.class);
        }
    };

    public static final FieldFilter COPYABLE_FIELDS = new FieldFilter() {

        @Override
        public boolean matches(Field field) {
            return !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()));
        }
    };

    private static boolean classesEquals(Class[] cls1, Class[] cls2) {
        if (cls1 == cls2)
            return true;
        if (cls1 == null || cls2 == null)
            return false;

        int length = cls1.length;
        if (cls2.length != length)
            return false;

        for (int i = 0; i < length; i++) {
            Class c1 = cls1[i];
            Class c2 = cls2[i];
            if (c1 == NULL.class || c2 == NULL.class) {
                continue;
            }
            if (c1.equals(c2) || c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1)) {
                continue;
            }
            if (castToWrapperClass(c1) == castToWrapperClass(c2)) {
                continue;
            }
            return false;
        }

        return true;
    }

    private static Class castToWrapperClass(Class clazz) {
        if (clazz == boolean.class) {
            return Boolean.class;
        }
        if (clazz == byte.class) {
            return Byte.class;
        }
        if (clazz == int.class) {
            return Integer.class;
        }
        if (clazz == long.class) {
            return Long.class;
        }
        if (clazz == double.class) {
            return Double.class;
        }
        if (clazz == float.class) {
            return Float.class;
        }
        if (clazz == short.class) {
            return Short.class;
        }
        if (clazz == char.class) {
            return Character.class;
        }
        return clazz;
    }

    private static class NULL {
    }



    public static <T> List<Class<? extends T>> getClasses(String packageName,Class<T> superClass) {
        Reflections reflections = new Reflections(packageName);
        return new ArrayList<>(reflections.getSubTypesOf(superClass));
    }


    public static <T> T singleton(Class<T> tClass, final Object... params) {
        Object instance = SINGLETON.get(tClass.getName());
        if (instance == null) {
            instance = newInstance(tClass, params);
            SINGLETON.put(tClass.getName(), instance);
        }
        return (T) instance;
    }

    public static Set<Class<?>> scanClasses(String packageName) throws IOException, ClassNotFoundException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        Set<Class<?>> classes = new HashSet<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                classes.addAll(findClasses(new File(resource.toURI()), packageName));
            } else if (resource.getProtocol().equals("jar")) {
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8.toString()))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(path) && name.endsWith(".class")) {
                            String className = name.replace('/', '.').substring(0, name.length() - 6);
                            classes.add(Class.forName(className, true, classLoader));
                        }
                    }
                }
            }
        }
        return classes;
    }

    private static Set<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                Class<?> cls = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                classes.add(cls);
            }
        }
        return classes;
    }

    


    public static Class<?> getUniqueChild(Set<Class<?>> classes,final Class<?> interfaceCls){
        final List<Class<?>> filtered = classes.stream()
                .filter(item -> interfaceCls.isAssignableFrom(item)
                        && item != interfaceCls)
                .collect(Collectors.toList());
        final int size = filtered.size();

        if (size < 1) {
            return null;
        }
        return 1 == size ? filtered.get(0) : null;
    }

    @SuppressWarnings("all")
    public static boolean isMatch(final Class<?> clazz, final Class<?> interfaceCls) {
        final Class<?>[] interfaces = clazz.getInterfaces();
        boolean match = Arrays.stream(interfaces)
                .anyMatch(item -> item.equals(interfaceCls));
        if (!match) {
            /* continue to check parent */
            if (Objects.nonNull(clazz.getSuperclass())) {
                match = isMatch(clazz.getSuperclass(), interfaceCls);
            }
        }
        return match;
    }


    static <T> void set(final Object instance, final String name, final T value, Logger logger) {
        Field field = null;
        try {
            field = instance.getClass().getDeclaredField(name);
            set(instance, field, value, logger);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> void set(final Object instance, final Field field, final T value, Logger logger) {
        CubeFn.safeNull(() -> CubeFn.safeJvm(() -> {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(instance, value);
        }, logger), instance, field);
    }

    public static Class<?> clazz(final String name) {
        return CubeFn.pool(Pool.CLASSES, name, () -> CubeFn.safeDefault(null,() -> Thread.currentThread()
                .getContextClassLoader().loadClass(name), name));
    }

    public static Field[] fields(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(item -> !Modifier.isStatic(item.getModifiers()))
                .filter(item -> !Modifier.isAbstract(item.getModifiers()))
                .toArray(Field[]::new);
    }

    private static Stream<Field> lookUp(final Class<?> clazz, final Class<?> fieldType) {
        return CubeFn.getDefault(null, () -> {
            final Field[] fields = fields(clazz);
            return Arrays.stream(fields)
                    .filter(field -> fieldType == field.getType() ||
                            fieldType == field.getType().getSuperclass() ||
                            isMatch(field.getType(), fieldType));
        });
    }

    static Field[] fieldAll(final Object instance, final Class<?> fieldType) {
        final Function<Class<?>, Set<Field>> lookupFun = clazz -> lookUp(clazz, fieldType)
                .collect(Collectors.toSet());
        return CubeFn.getDefault(null, () -> fieldAll(instance.getClass(), fieldType).toArray(new Field[]{}),
                instance, fieldType);
    }

    private static Set<Field> fieldAll(final Class<?> clazz, final Class<?> fieldType) {
        final Set<Field> fieldSet = new HashSet<>();
        if (Object.class != clazz) {
            fieldSet.addAll(lookUp(clazz, fieldType).collect(Collectors.toSet()));
            fieldSet.addAll(fieldAll(clazz.getSuperclass(), fieldType));
        }
        return fieldSet;
    }


    public static <T> Field contract(final Class<?> executor, final T instance, final Class<?> fieldType, Logger logger) {
        final Field[] fields = fieldAll(instance, fieldType);
        /*
         * Counter
         */
        final Field[] filtered = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Contract.class))
                .toArray(Field[]::new);
        CubeFn.outError(logger, 1 != filtered.length, com.cl.framework.core.exception.DataTableException.class, ErrorCodeEnum.CONTRACT_FIELD_ERROR
                , MessageFormat.format(ErrorInfoConstant.CONTRACT_FIELD_ERROR, executor, fieldType, instance.getClass(), filtered.length));
        return filtered[0];
    }

    public static <T, V> void contract(final Class<?> executor, final T instance, final Class<?> fieldType, final V value,Logger logger) {
        final Field field = contract(executor, instance, fieldType, logger);
        setField(field, instance, value);
    }


    public static <T> T[] add(final T[] array, final T element) {
        final Class<?> type;
        if (array != null) {
            type = array.getClass().getComponentType();
        } else if (element != null) {
            type = element.getClass();
        } else {
            throw new IllegalArgumentException("Arguments cannot both be null");
        }
        @SuppressWarnings("unchecked") // type must be T
        final T[] newArray = (T[]) copyArrayGrow1(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    private static Object copyArrayGrow1(final Object array, final Class<?> newArrayComponentType) {
        if (array != null) {
            final int arrayLength = Array.getLength(array);
            final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }

    public static Set<Class<?>> getByAnno(String pkg, Annotation c){
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage(pkg)
                .setScanners(Scanners.TypesAnnotated));

        return reflections.getTypesAnnotatedWith(c);
    }

}
