package com.cl.framework.core.options.converter;

import com.cl.framework.core.options.OriginSelectorOptions;
import io.vertx.core.json.JsonObject;

/**
 * OriginSelectorOptionsConverter
 * @author xhz
 */
public final class OriginSelectorOptionsConverter {


    private OriginSelectorOptionsConverter() {
    }

    public static void fromJson(final JsonObject json, final OriginSelectorOptions obj) {
        if (json.getValue("port") != null && json.getValue("port") instanceof Integer) {
            obj.setPort(json.getInteger("port"));
        }

        if (json.getValue("host") != null && json.getValue("host") instanceof String) {
            obj.setHost(json.getString("host"));
        }

        if (json.getValue("regexPath") != null && json.getValue("regexPath") instanceof String) {
            obj.setRegexPath(json.getString("regexPath"));
        }
    }

}
