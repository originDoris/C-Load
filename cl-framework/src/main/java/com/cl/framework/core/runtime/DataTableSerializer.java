package com.cl.framework.core.runtime;

import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.param.serialization.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 按照不同的type 进行序列化
 * @author xhz
 */
public class DataTableSerializer {

    @SuppressWarnings("all")
    private static final ConcurrentMap<Class<?>, FieldSerializable> SABERS =
            new ConcurrentHashMap<Class<?>, FieldSerializable>() {
                {
                    this.put(int.class, ReflectionUtils.singleton(IntegerSaber.class));
                    this.put(Integer.class, ReflectionUtils.singleton(IntegerSaber.class));
                    this.put(short.class, ReflectionUtils.singleton(ShortSaber.class));
                    this.put(Short.class, ReflectionUtils.singleton(ShortSaber.class));
                    this.put(long.class, ReflectionUtils.singleton(LongSaber.class));
                    this.put(Long.class, ReflectionUtils.singleton(LongSaber.class));

                    this.put(double.class, ReflectionUtils.singleton(DoubleSaber.class));
                    this.put(Double.class, ReflectionUtils.singleton(DoubleSaber.class));

                    this.put(LocalDate.class, ReflectionUtils.singleton(Java8DateTimeSaber.class));
                    this.put(LocalDateTime.class, ReflectionUtils.singleton(Java8DateTimeSaber.class));
                    this.put(LocalTime.class, ReflectionUtils.singleton(Java8DateTimeSaber.class));

                    this.put(float.class, ReflectionUtils.singleton(FloatSaber.class));
                    this.put(Float.class, ReflectionUtils.singleton(FloatSaber.class));
                    this.put(BigDecimal.class, ReflectionUtils.singleton(BigDecimalSaber.class));

                    this.put(Enum.class, ReflectionUtils.singleton(EnumSaber.class));

                    this.put(boolean.class, ReflectionUtils.singleton(BooleanSaber.class));
                    this.put(Boolean.class, ReflectionUtils.singleton(BooleanSaber.class));

                    this.put(Date.class, ReflectionUtils.singleton(DateSaber.class));
                    this.put(Calendar.class, ReflectionUtils.singleton(DateSaber.class));

                    this.put(JsonObject.class, ReflectionUtils.singleton(JsonObjectSaber.class));
                    this.put(JsonArray.class, ReflectionUtils.singleton(JsonArraySaber.class));

                    this.put(String.class, ReflectionUtils.singleton(StringSaber.class));
                    this.put(StringBuffer.class, ReflectionUtils.singleton(StringBufferSaber.class));
                    this.put(StringBuilder.class, ReflectionUtils.singleton(StringBufferSaber.class));

                    this.put(Buffer.class, ReflectionUtils.singleton(BufferSaber.class));
                    this.put(Set.class, ReflectionUtils.singleton(CollectionSaber.class));
                    this.put(List.class, ReflectionUtils.singleton(CollectionSaber.class));
                    this.put(Collection.class, ReflectionUtils.singleton(CollectionSaber.class));

                    this.put(byte[].class, ReflectionUtils.singleton(ByteArraySaber.class));
                    this.put(Byte[].class, ReflectionUtils.singleton(ByteArraySaber.class));

                    this.put(File.class, ReflectionUtils.singleton(FileSaber.class));
                }
            };

    public static Object getValue(final Class<?> paramType,
                                  final String literal) {
        Object reference = null;
        if (null != literal) {
            FieldSerializable saber;
            if (paramType.isEnum()) {
                saber = SABERS.get(Enum.class);
            } else if (Collection.class.isAssignableFrom(paramType)) {
                saber = SABERS.get(Collection.class);
            } else {
                saber = SABERS.get(paramType);
            }
            if (null == saber) {
                saber = ReflectionUtils.singleton(CommonSaber.class);
            }
            reference = saber.from(paramType, literal);
        }
        return reference;
    }

    public static <T> boolean isDirect(final T input) {
        boolean result = false;
        if (null != input) {
            final Class<?> cls = input.getClass();
            if (JsonObject.class == cls) {
                result = false;
            } else if (JsonArray.class == cls) {
                result = false;
            } else {
                result = SABERS.containsKey(cls);
            }
        }
        return result;
    }

    public static <T> Object toSupport(final T input) {
        try {
            Object reference = null;
            if (null != input) {
                FieldSerializable saber;
                final Class<?> cls = input.getClass();
                if (cls.isEnum()) {
                    saber = SABERS.get(Enum.class);
                } else if (Calendar.class.isAssignableFrom(cls)) {
                    saber = SABERS.get(Date.class);
                } else if (Collection.class.isAssignableFrom(cls)) {
                    saber = SABERS.get(Collection.class);
                } else if (cls.isArray()) {
                    final Class<?> type = cls.getComponentType();
                    if (byte.class == type || Byte.class == type) {
                        saber = SABERS.get(byte[].class);
                    } else {
                        saber = SABERS.get(Collection.class);
                    }
                } else {
                    saber = SABERS.get(cls);
                }
                if (null == saber) {
                    saber = ReflectionUtils.singleton(CommonSaber.class);
                }
                reference = saber.from(input);
            }
            return reference;
        } catch (final Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
