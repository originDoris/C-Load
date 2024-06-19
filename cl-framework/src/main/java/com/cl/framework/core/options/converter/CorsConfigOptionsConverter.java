package com.cl.framework.core.options.converter;

import com.cl.framework.core.options.CorsConfigOptions;
import com.cl.framework.core.options.HazelcastClusterOptions;
import com.cl.framework.core.options.OriginSelectorOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;

/**
 * CorsConfigOptionsConverter
 *
 * @author xhz
 */
public final class CorsConfigOptionsConverter {

    private CorsConfigOptionsConverter() {
    }

    public static void fromJson(final JsonObject json, final CorsConfigOptions obj) {
        if (json.getValue("credentials") != null && json.getValue("credentials") instanceof Boolean) {
            obj.setCredentials(json.getBoolean("credentials"));
        }

        if (json.getValue("origin") != null && json.getValue("origin") instanceof String) {
            obj.setOrigin(json.getString("origin"));
        }


        if (json.getValue("methods") != null && json.getValue("methods") instanceof JsonArray) {
            ArrayList<String> methods = new ArrayList<>();
            JsonArray jsonArray = json.getJsonArray("methods");
            for (Object item : jsonArray) {
                if (item instanceof String) {
                    methods.add((String) item);
                }
            }
            obj.setMethods(methods);
        }

        if (json.getValue("headers") != null && json.getValue("headers") instanceof JsonArray) {
            ArrayList<String> headers = new ArrayList<>();
            JsonArray jsonArray = json.getJsonArray("headers");
            for (Object item : jsonArray) {
                if (item instanceof String) {
                    headers.add((String) item);
                }
            }
            obj.setHeaders(headers);
        }
    }
}
