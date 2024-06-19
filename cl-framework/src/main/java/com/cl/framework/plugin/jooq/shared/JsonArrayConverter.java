package com.cl.framework.plugin.jooq.shared;

import io.vertx.core.json.JsonArray;
import org.jooq.Converter;

/**
 * 将字符串转换为jsonArray
 * @author xhz
 */
public class JsonArrayConverter implements Converter<String,JsonArray> {

    @Override
    public JsonArray from(String databaseObject) {
        return databaseObject==null?null:new JsonArray(databaseObject);
    }

    @Override
    public String to(JsonArray userObject) {
        return userObject==null?null:userObject.encode();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<JsonArray> toType() {
        return JsonArray.class;
    }
}
