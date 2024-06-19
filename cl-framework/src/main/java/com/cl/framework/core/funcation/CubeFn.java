package com.cl.framework.core.funcation;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 调用函数的唯一接口
 *
 * @author xhz
 */
@Slf4j
public final class CubeFn {


    private CubeFn() {
    }


    public static void onRun(Logger logger, Actuator actuator) {
        Announce.toRun(logger, actuator);
    }

    public static void safeSemi(final boolean condition, final SafeActuator tSupplier, Logger logger) {

        Semi.exec(condition, tSupplier, null, logger);
    }

    public static void safeSemi(final boolean condition, final SafeActuator tSupplier, final SafeActuator fSupplier, final Logger logger) {
        Semi.exec(condition, tSupplier, fSupplier, logger);
    }


    public static void outError(Logger logger, final boolean condition, final Class<? extends com.cl.framework.core.exception.DataTableException> upClass, final Object... args) {
        if (condition) {
            Announce.outUp(upClass, logger, args);
        }
    }

    public static void safeNull(final Actuator actuator, final Object... input) {
        if (isSatisfy(input)) {
            try {
                actuator.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static <T> T getNull(final T defaultValue, final Supplier<T> supplier) {
        return Semi.execReturn(supplier, defaultValue);
    }


    private static boolean isSatisfy(final Object... input) {
        return 0 == input.length || Arrays.stream(input).allMatch(Objects::nonNull);
    }

    public static <T> T getDefault(T defaultValue, final Supplier<T> supplier, final Object... input) {
        if (Arrays.stream(input).allMatch(Objects::nonNull)) {
            final T ret = supplier.get();
            return (null == ret) ? defaultValue : ret;
        } else {
            return defaultValue;
        }
    }

    public static <T> T safeDefault(T defaultValue, final SafeSupplier<T> supplier, final Object... input) {
        try {
            if (Arrays.stream(input).allMatch(Objects::nonNull)) {
                final T ret = supplier.get();
                return (null == ret) ? defaultValue : ret;
            } else {
                return defaultValue;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T getSemi(final boolean condition, final Logger logger, final Supplier<T> tSupplier, final Supplier<T> fSupplier) {
        return Defend.dataCubeReturn(() -> Semi.execDataTable(condition, tSupplier, fSupplier), logger);
    }

    public static void safeJvm(final Actuator actuator, final Logger logger) {
        Defend.dataCubeVoid(actuator, logger);
    }


    public static <K, V> V pool(final ConcurrentMap<K, V> pool, final K key, final Supplier<V> poolFn){
        if (Objects.isNull(key)) {
            throw new com.cl.framework.core.exception.DataTableException("pool 方法 key不能为空！");
        }
        V reference = pool.get(key);
        if (null == reference) {
            reference = poolFn.get();
            if (null != reference) {
                pool.put(key, reference);
            }
        }
        return reference;
    }

    public static <V> V poolThread(final ConcurrentMap<String, V> pool, final Supplier<V> poolFn) {
        final String threadName = Thread.currentThread().getName();
        return pool(pool, threadName, poolFn);
    }

    public static <T extends Enum<T>> T toEnum(final Supplier<String> supplier, Class<T> type, T defaultEnum, Logger logger) {
        String data = supplier.get();
        T t = null;
        try {
            t = Enum.valueOf(type, data);
        } catch (Exception e) {
            logger.warn(MessageFormat.format("gat Enum Value error ,type:{0},data:{1},error:{2}", type.getName(), data, e.getMessage()));
        } finally {
            if (t == null) {
                t = defaultEnum;
            }
        }
        return t;
    }


    public static <K, V> void exec(final ConcurrentMap<K, V> map, final BiConsumer<K, V> consumer) {
        map.forEach((key, value) -> {
            if (null != key && null != value) {
                consumer.accept(key, value);
            }
        });
    }

    public static <T> T find(final List<T> list, final Predicate<T> fnFilter, Logger logger) {
        return CubeFn.getDefault(null, () -> {
            final List<T> filtered = list.stream().filter(fnFilter).collect(Collectors.toList());
            return CubeFn.getSemi(filtered.isEmpty(), logger,
                    () -> null,
                    () -> filtered.get(0));
        }, list, fnFilter);
    }


    public static <V> void itArray(final V[] array, final BiConsumer<V, Integer> fnEach) {
        exec(Arrays.asList(array), fnEach);
    }

    public static <V> void itSet(final Set<V> set, final BiConsumer<V, Integer> fnEach) {
        final List<V> list = new ArrayList<>(set);
        exec(list, fnEach);
    }


   public static <V> void exec(final List<V> list, final BiConsumer<V, Integer> consumer) {
        final int size = list.size();
        for (int idx = 0; idx < size; idx++) {
            final V item = list.get(idx);
            if (null != item) {
                consumer.accept(item, idx);
            }
        }
    }

    public static <K, V> ConcurrentMap<K, V> reduce(final Set<K> from, final ConcurrentMap<K, V> to) {
        final ConcurrentMap<K, V> result = new ConcurrentHashMap<>();
        from.forEach((key) -> {
            final V value = to.get(key);
            if (null != value) {
                result.put(key, value);
            }
        });
        return result;
    }

    public static <K, T, V> ConcurrentMap<K, V> reduce(final ConcurrentMap<K, T> from, final ConcurrentMap<T, V> to) {
        final ConcurrentMap<K, V> result = new ConcurrentHashMap<>();
        from.forEach((key, middle) -> {
            final V value = to.get(middle);
            if (null != value) {
                result.put(key, value);
            }
        });
        return result;
    }

    public static <T> void exec(final JsonArray dataArray, final Class<T> clazz, final BiConsumer<T, Integer> fnIt) throws com.cl.framework.core.exception.DataTableException {
        final int size = dataArray.size();
        for (int idx = 0; idx < size; idx++) {
            final Object value = dataArray.getValue(idx);
            if (null != value) {
                if (clazz == value.getClass()) {
                    final T item = (T) value;
                    fnIt.accept(item, idx);
                }
            }
        }
    }

   public static void exec(final Integer times, final SafeActuator actuator) {
        int start = 0;
        while (start < times) {
            actuator.execute();
            start++;
        }
    }

   public static <T> void exec(final JsonObject data, final BiConsumer<T, String> fnIt) throws com.cl.framework.core.exception.DataTableException {
        for (final String name : data.fieldNames()) {
            final Object item = data.getValue(name);
            if (null != item) {
                fnIt.accept((T) item, name);
            }
        }
    }

   public static <T> JsonArray toJArray(final Set<T> set) {
        final JsonArray array = new JsonArray();
        if (Objects.nonNull(set)) {
            set.stream().filter(Objects::nonNull).forEach(array::add);
        }
        return array;
    }

   public static void itRepeat(final Integer times, final Actuator actuator) {
       try {
           int start = 0;
           while (start < times) {
               actuator.execute();
               start++;
           }
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }
}
