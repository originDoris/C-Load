package com.cl.framework.core.web.core.param.serialization;

import io.vertx.core.json.JsonArray;

import java.util.function.Function;

/**
 * JsonArray
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class JsonArraySaber extends JsonSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return JsonArray.class == paramType;
    }

    @Override
    protected Function<String, JsonArray> getFun() {
        return JsonArray::new;
    }
}
