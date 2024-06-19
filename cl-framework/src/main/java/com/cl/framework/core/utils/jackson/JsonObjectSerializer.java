package com.cl.framework.core.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

/**
 * jsonObject 序列化
 * @author xhz
 */
public class JsonObjectSerializer extends JsonSerializer<JsonObject> {
    @Override
    public void serialize(final JsonObject value,
                          final JsonGenerator jgen,
                          final SerializerProvider provider) throws IOException {
        jgen.writeObject(value.getMap());
    }
}