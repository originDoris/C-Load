package com.cl.framework.core.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ClassDeserializer
 * @author xhz
 */
public class ClassDeserializer extends JsonDeserializer<Class<?>> {
   public static final  ConcurrentMap<String, Class<?>> CLASSES = new ConcurrentHashMap<>();

    @Override
    public Class<?> deserialize(final JsonParser parser,
                                final DeserializationContext context)
            throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return clazz(node.asText().trim(), null);
    }


    static Class<?> clazz(final String name, final Class<?> defaultCls) {
        if (StringUtils.isBlank(name)) {
            return defaultCls;
        } else {
            try {
                Class<?> clazz = CLASSES.get(name);
                if (Objects.isNull(clazz)) {
                    clazz = Thread.currentThread().getContextClassLoader().loadClass(name);
                    CLASSES.put(name, clazz);
                }
                if (Objects.isNull(clazz)) {
                    return defaultCls;
                } else {
                    return clazz;
                }
            } catch (final Throwable ex) {
                return defaultCls;
            }
        }
    }
}
