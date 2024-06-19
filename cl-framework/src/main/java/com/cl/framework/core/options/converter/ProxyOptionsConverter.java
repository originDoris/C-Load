package com.cl.framework.core.options.converter;

import com.cl.framework.core.options.OriginSelectorOptions;
import com.cl.framework.core.options.ProxyOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;

/**
 * ProxyOptionsConverter
 * @author xhz
 */
public final class ProxyOptionsConverter {


    private ProxyOptionsConverter() {
    }

    public static void fromJson(final JsonObject json, final ProxyOptions obj) {
        if (json.getValue("port") != null && json.getValue("port") instanceof Integer) {
            obj.setPort(json.getInteger("port"));
        }

        if (json.getValue("rootPath") != null && json.getValue("rootPath") instanceof String) {
            obj.setRootPath(json.getString("rootPath"));
        }


        if (json.getValue("origins") != null && json.getValue("origins") instanceof JsonArray) {
            ArrayList<OriginSelectorOptions> options = new ArrayList<>();
            JsonArray jsonArray = json.getJsonArray("origins");
            for (Object item : jsonArray) {
                if (item instanceof JsonObject) {
                    options.add(new OriginSelectorOptions((JsonObject) item));
                }
            }
            obj.setOrigins(options);
        }

        if (json.getValue("authPath") != null && json.getValue("authPath") instanceof JsonArray) {
            ArrayList<String> options = new ArrayList<>();
            JsonArray jsonArray = json.getJsonArray("authPath");
            for (Object item : jsonArray) {
                if (item instanceof String) {
                    options.add((String) item);
                }
            }
            obj.setAuthPath(options);
        }

        if (json.getValue("excludeAuthPath") != null && json.getValue("excludeAuthPath") instanceof JsonArray) {
            ArrayList<String> options = new ArrayList<>();
            JsonArray jsonArray = json.getJsonArray("excludeAuthPath");
            for (Object item : jsonArray) {
                if (item instanceof String) {
                    options.add((String) item);
                }
            }
            obj.setExcludeAuthPath(options);
        }
    }

}
