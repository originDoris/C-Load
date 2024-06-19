package com.cl.framework.core.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.json.JsonArray;

import java.io.IOException;

/**
 * JsonArray的反序列化
 * @author xhz
 */
public class JsonArrayDeserializer extends JsonDeserializer<JsonArray> {

    @Override
    public JsonArray deserialize(final JsonParser parser,
                                 final DeserializationContext context)
            throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return new JsonArray(node.toString());
    }
}
