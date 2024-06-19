package com.cl.framework.core.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * ClassSerializer
 * @author xhz
 */
public class ClassSerializer extends JsonSerializer<Class<?>> {

    @Override
    public void serialize(final Class<?> clazz, final JsonGenerator generator,
                          final SerializerProvider provider)
            throws IOException {
        generator.writeString(clazz.getName());
    }
}
