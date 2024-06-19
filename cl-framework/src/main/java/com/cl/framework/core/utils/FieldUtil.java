package com.cl.framework.core.utils;

import com.cl.framework.core.funcation.CubeFn;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.ext.web.FileUpload;

import java.io.File;
import java.text.MessageFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 字段相关工具类
 *
 * @author xhz
 */
public class FieldUtil {

   public static final ConcurrentMap<Class<?>, Class<?>> UNBOXES =
            new ConcurrentHashMap<Class<?>, Class<?>>() {
                {
                    this.put(Integer.class, int.class);
                    this.put(Long.class, long.class);
                    this.put(Short.class, short.class);
                    this.put(Boolean.class, boolean.class);
                    this.put(Character.class, char.class);
                    this.put(Double.class, double.class);
                    this.put(Float.class, float.class);
                    this.put(Byte.class, byte.class);
                }
            };

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldUtil.class);

    public static Class<?> toPrimary(final Class<?> source) {
        return UNBOXES.getOrDefault(source, source);
    }

    public static boolean isBoolean(final Object value) {
        return CubeFn.getSemi(null == value, LOGGER,
                () -> false,
                () -> {
                    boolean logical = false;
                    final String literal = value.toString();
                    // Multi true literal such as "true", "TRUE" or 1
                    if ("true".equalsIgnoreCase(literal)
                            || Integer.valueOf(1).toString().equalsIgnoreCase(literal)
                            || "false".equalsIgnoreCase(literal)
                            || Integer.valueOf(0).toString().equalsIgnoreCase(literal)) {
                        logical = true;
                    }
                    return logical;
                });
    }


    public static boolean isDate(final Object value) {
        if (Objects.isNull(value)) {
            return false;
        } else {
            if (value instanceof Class) {
                final Class<?> type = (Class<?>) value;
                return LocalDateTime.class == type || LocalDate.class == type ||
                        LocalTime.class == type || Date.class == type ||
                        Instant.class == type;
            } else {
                return DateUtil.isValid(value.toString());
            }
        }
    }

    public static boolean isDecimal(final String original) {
        return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", original);
    }


    private static boolean isMatch(final String regex, final String original) {
        return CubeFn.getDefault(null, () -> {
            return RegexUtil.regex(regex, original);
        }, regex, original);
    }

    public static boolean isInteger(final String original) {
        return isMatch("[+-]{0,1}0", original) || isPositive(original) || isNegative(original);
    }
    public static boolean isInteger(final Object original) {
        return isInteger(original.toString());
    }

    public static boolean isPositive(final String original) {
        return isMatch("^\\+{0,1}[0-9]\\d*", original);
    }

    public static boolean isNegative(final String original) {
        return isMatch("^-[0-9]\\d*", original);
    }


   public static boolean isImplement(final Class<?> clazz, final Class<?> interfaceCls) {
        final Class<?>[] interfaces = clazz.getInterfaces();
        boolean match = Arrays.asList(interfaces).contains(interfaceCls);
        if (!match) {
            /* continue to check parent */
            if (Objects.nonNull(clazz.getSuperclass())) {
                match = isImplement(clazz.getSuperclass(), interfaceCls);
            }
        }
        return match;
    }

    public static <V> void itMatrix(final V[][] array, final Consumer<V> fnEach) {
       exec(array, fnEach);
    }

    static <V> void exec(final V[][] matrix, final Consumer<V> consumer) {
        for (final V[] arr : matrix) {
            for (final V item : arr) {
                if (null != item) {
                    consumer.accept(item);
                }
            }
        }
    }

    public static ConcurrentMap<String, Set<FileUpload>> toFile(final List<FileUpload> fileUploads) {
        return file(fileUploads);
    }
    static ConcurrentMap<String, Set<FileUpload>> file(final List<FileUpload> fileUploads) {
        final ConcurrentMap<String, Set<FileUpload>> fileMap = new ConcurrentHashMap<>();
        fileUploads.forEach(fileUpload -> {
            final String field = fileUpload.name();
            /* Process */
            if (!fileMap.containsKey(field)) {
                final Set<FileUpload> set = new HashSet<>();
                fileMap.put(field, set);
            }
            final Set<FileUpload> set = fileMap.get(field);
            set.add(fileUpload);
        });
        return fileMap;
    }


   public static <T> T toFile(final List<FileUpload> fileUploads, final Class<?> expected, final Function<String, Buffer> consumer) {
        if (Objects.isNull(fileUploads) || fileUploads.isEmpty()) {
            LOGGER.warn("The fileUploads set size is 0.");
            if (Collection.class.isAssignableFrom(expected)) {
                if (List.class.isAssignableFrom(expected)) {
                    return (T) Collections.emptyList();
                } else if (Set.class.isAssignableFrom(expected)) {
                    return (T) Collections.emptySet();
                } else {
                    LOGGER.warn(MessageFormat.format("The type {0} is not supported.", expected.getName()));
                    return null;
                }
            } else {
                return null;
            }
        } else {
            if (Collection.class.isAssignableFrom(expected)) {

                final Stream stream = fileUploads.stream()
                        .map(fileUpload -> toFile(fileUpload, FileUpload.class, consumer));
                if (List.class.isAssignableFrom(expected)) {
                    /*
                     * List<T>
                     */
                    return (T) stream.collect(Collectors.toList());
                } else if (Set.class.isAssignableFrom(expected)) {
                    /*
                     * Set<T>
                     */
                    return (T) stream.collect(Collectors.toSet());
                } else {
                    LOGGER.warn(MessageFormat.format("The type {0} is not supported.", expected.getName()));
                    return null;
                }
            } else {
                /*
                 * Size > 1 ( Because declared type is not collection )
                 */
                if (!isByteArray(expected) && expected.isArray()) {
                    /*
                     * expected != byte[]/Byte[]
                     */
                    if (expected.isArray()) {
                        /*
                         * expected == T[]
                         */
                        final Class<?> componentCls = expected.getComponentType();
                        final List<FileUpload> fileList = new ArrayList<>(fileUploads);

                        if (File.class == componentCls) {
                            /*
                             * File[]
                             */
                            final File[] files = new File[fileList.size()];
                            for (int idx = 0; idx < fileList.size(); idx++) {
                                files[idx] = toFile(fileList.get(idx), File.class, consumer);
                            }
                            return (T) files;
                        } else if (io.vertx.ext.web.FileUpload.class.isAssignableFrom(componentCls)) {
                            /*
                             * FileUpload[]
                             */
                            final FileUpload[] files = new FileUpload[fileList.size()];
                            for (int idx = 0; idx < fileList.size(); idx++) {
                                files[idx] = fileList.get(idx);
                            }
                            return (T) files;
                        }
                        return (T) fileUploads.stream()
                                .map(item -> toFile(item, componentCls, consumer))
                                .toArray();
                    } else {
                        /*
                         * expected = Single
                         */
                        return toFile(fileUploads, expected, consumer);
                    }
                } else {
                    /*
                     * byte[] or single type
                     */
                    final FileUpload fileUpload = fileUploads.iterator().next();
                    return toFile(fileUpload, expected, consumer);
                }
            }
        }
    }

    private static boolean isByteArray(final Class<?> expected) {
        if (expected.isArray()) {
            final Class<?> componentCls = expected.getComponentType();
            return (byte.class == componentCls || Byte.class == componentCls);
        } else return false;
    }


    static <T> T toFile(final FileUpload fileUpload, final Class<?> expected, final Function<String, Buffer> consumer) {
        final String filename = fileUpload.uploadedFileName();
        if (FileUpload.class.isAssignableFrom(expected)) {
            return (T) fileUpload;
        } else if (File.class == expected) {
            return (T) com.cl.framework.core.runtime.DataTableSerializer.getValue(expected, filename);
        } else if (expected.isArray()) {
            final Class<?> componentCls = expected.getComponentType();
            if (isByteArray(expected)) {
                final Buffer buffer = consumer.apply(filename);
                final byte[] bytes = buffer.getBytes();
                if (byte.class == componentCls) {
                    return (T) bytes;
                } else {
                    final Byte[] byteWrapper = new Byte[bytes.length];
                    for (int idx = 0; idx < bytes.length; idx++) {
                        byteWrapper[idx] = bytes[idx];
                    }
                    return (T) byteWrapper;
                }
            } else {
                LOGGER.warn(MessageFormat.format("The array type support byte[]/Byte[] only in current version, current = {0}", componentCls.getName()));
                return null;
            }
        } else if (Buffer.class.isAssignableFrom(expected)) {
            /*
             * Buffer
             */
            return (T) consumer.apply(filename);
        } else {
            LOGGER.warn(MessageFormat.format("The expected type {0} is not supported.", expected.getName()));
            return null;
        }
    }

    public static String fromJoin(final Object[] input, final String separator) {
        return join(input, separator);
    }

    static String join(final Object[] input, final String separator) {
        final Set<String> hashSet = new HashSet<>();
        Arrays.stream(input).filter(Objects::nonNull)
                .map(Object::toString).forEach(hashSet::add);
        return join(hashSet, separator);
    }

    static String join(final Collection<String> input, final String separator) {
        final String connector = (null == separator) ? "," : separator;
        return CubeFn.getDefault(null, () -> {
            final StringBuilder builder = new StringBuilder();
            final int size = input.size();
            int start = 0;
            for (final String item : input) {
                builder.append(item);
                start++;
                if (start < size) {
                    builder.append(connector);
                }
            }
            return builder.toString();
        }, input);
    }


   public static String toString(final Object reference) {
        return CubeFn.getDefault("null", () -> {
            final String literal;
            if (isJObject(reference)) {
                literal =  JsonUtil.toJson(reference);
            }  else {
                literal = reference.toString();
            }
            return literal;
        }, reference);
    }

   public static boolean isJArray(final Object value) {
        return CubeFn.getSemi(null == value, LOGGER,
                () -> false,
                () -> isJArray(value.getClass()));
    }

    static boolean isJArray(final Class<?> clazz) {
        return JsonArray.class == clazz || List.class == clazz;
    }



   public static boolean isJObject(final Object value) {
        return CubeFn.getSemi(null == value, LOGGER,
                () -> false,
                () -> isJObject(value.getClass()));
    }


    public static <T> int compareTo(
            final T left, final T right,
            final BiFunction<T, T, Integer> compare) {
        if (null == left && null == right) {
            return 0;
        } else if (null == left && null != right) {
            return -1;
        } else if (null != left && null == right) {
            return 1;
        } else {
            return compare.apply(left, right);
        }
    }

    public static int compareTo(final String left, final String right) {
        return compareTo(left, right,
                String::compareTo);
    }

    public static int compareTo(final int left, final int right) {
        return left - right;
    }

    static boolean isJObject(final Class<?> clazz) {
        return !clazz.isPrimitive() && !isWrapperType(clazz) && !clazz.equals(Collection.class) && !clazz.equals(JsonArray.class);
    }
    static boolean isWrapperType(Class<?> clazz) {
        return clazz.equals(Boolean.class)
                || clazz.equals(Integer.class)
                || clazz.equals(Character.class)
                || clazz.equals(Byte.class)
                || clazz.equals(Short.class)
                || clazz.equals(Double.class)
                || clazz.equals(Long.class)
                || clazz.equals(Float.class)
                || clazz.equals(String.class);
    }

    static boolean isArray(final Object value) {
        if (null == value) {
            return false;
        }
        return (value instanceof Collection ||
                value.getClass().isArray());
    }

   public static Collection<?> toCollection(final Object value) {
        return CubeFn.getDefault(null, () -> {
            // Collection
            if (value instanceof Collection) {
                return ((Collection<?>) value);
            }
            // JsonArray
            if (isJArray(value)) {
                return ((JsonArray) value).getList();
            }
            // Object[]
            if (isArray(value)) {
                // Array
                final Object[] values = (Object[]) value;
                return Arrays.asList(values);
            }
            return null;
        }, value);
    }
}

