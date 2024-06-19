package com.cl.framework.plugin.jooq.shared;

import io.vertx.core.json.JsonObject;
import org.jooq.Converter;

/**
 * 将字符串类型转换为JsonObject
 * @author xhz
 */
public class JsonObjectConverter implements Converter<String,JsonObject> {

    @Override
    public JsonObject from(String databaseObject) {
        return databaseObject == null?null:new JsonObject(databaseObject);
    }

    @Override
    public String to(JsonObject userObject) {
        return userObject==null?null:userObject.encode();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<JsonObject> toType() {
        return JsonObject.class;
    }
}
