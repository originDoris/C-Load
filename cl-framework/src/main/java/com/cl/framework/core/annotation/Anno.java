package com.cl.framework.core.annotation;

import com.cl.framework.core.enums.ServerType;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.impl.logging.Logger;

import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 该类用来扫描注解
 *
 * @author xhz
 */
public final class Anno {
    private Anno() {
    }


    public static ConcurrentMap<String, Annotation> get(final Class<?> clazz) {
        Annotation[] declaredAnnotations = clazz.getDeclaredAnnotations();
        ConcurrentHashMap<String, Annotation> map = new ConcurrentHashMap<>();
        for (Annotation annotation : declaredAnnotations) {
            map.put(annotation.annotationType().getName(), annotation);
        }
        return map;
    }


    public static StartUp getStartUp(Annotation annotation) {
        return (annotation instanceof StartUp) ? (StartUp) annotation : null;
    }

    public static Path getPath(Annotation annotation) {
        return (annotation instanceof Path) ? (Path) annotation : null;
    }

    public static Path getPath(final Class<?> clazz) {
        return getPath(clazz.getDeclaredAnnotation(Path.class));
    }


    public static Initial getInitial(final Class<?> clazz) {
        return getInitial(clazz.getDeclaredAnnotation(Initial.class));
    }

    public static Initial getInitial(Annotation annotation) {
        return (annotation instanceof Initial) ? (Initial) annotation : null;
    }


    public static Destroy getDestroy(final Class<?> clazz) {
        return getDestroy(clazz.getDeclaredAnnotation(Destroy.class));
    }

    public static Destroy getDestroy(Annotation annotation) {
        return (annotation instanceof Destroy) ? (Destroy) annotation : null;
    }


    public static Path getPath(final Method method) {
        return getPath(method.getDeclaredAnnotation(Path.class));
    }

    public static Annotation[] query(final Class<?> clazz,
                                     final Class<? extends Annotation> methodCls) {
        return CubeFn.getDefault(null
                , () -> Arrays.stream(clazz.getDeclaredMethods())
                        .filter(item -> item.isAnnotationPresent(methodCls))
                        .map(item -> item.getAnnotation(methodCls))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()).toArray(new Annotation[]{}), clazz, methodCls);
    }


    public static ServerType getAgentKey(final Class<?> clazz, Logger logger) {
        return CubeFn.getSemi(clazz.isAnnotationPresent(Agent.class), logger,
                () -> ReflectionUtils.invoke(clazz.getDeclaredAnnotation(Agent.class), "type"),
                () -> null);
    }
}
