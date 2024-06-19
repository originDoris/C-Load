package com.cl.framework.core.web.core.param.serialization;

import io.vertx.core.json.JsonObject;

import java.util.LinkedHashMap;
import java.util.function.Function;

/**
 * JsonObject
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class JsonObjectSaber extends JsonSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return JsonObject.class == paramType || LinkedHashMap.class == paramType;
    }

    @Override
    protected Function<String, JsonObject> getFun() {
        return JsonObject::new;
    }
}
