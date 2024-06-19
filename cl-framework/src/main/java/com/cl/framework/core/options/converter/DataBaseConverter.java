package com.cl.framework.core.options.converter;


import com.cl.framework.core.enums.DatabaseType;
import com.cl.framework.core.options.DataBaseOptions;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

/**
 * DataBaseConverter
 *
 * @author xhz
 */
public class DataBaseConverter {
    public static void fromJson(final JsonObject json, final DataBaseOptions obj) {
        if (json.getValue("options") != null && json.getValue("options") instanceof JsonObject) {
            obj.setOptions(json.getJsonObject("options"));
        }
        if (json.getValue("hostname") != null && json.getValue("hostname") instanceof String) {
            obj.setHostname(json.getString("hostname"));
        }

        if (json.getValue("instance") != null && json.getValue("instance") instanceof String) {
            obj.setInstance(json.getString("instance"));
        }
        if (json.getValue("port") != null && json.getValue("port") instanceof Integer) {
            obj.setPort(json.getInteger("port"));
        }

        if (json.getValue("category") != null && json.getValue("category") instanceof String) {
            Optional<DatabaseType> category = DatabaseType.get(json.getString("category"));
            category.ifPresent(obj::setCategory);
        }

        if (json.getValue("jdbcUrl") != null && json.getValue("jdbcUrl") instanceof String) {
            obj.setJdbcUrl(json.getString("jdbcUrl"));
        }
        if (json.getValue("username") != null && json.getValue("username") instanceof String) {
            obj.setUsername(json.getString("username"));
        }

        if (json.getValue("password") != null && json.getValue("password") instanceof String) {
            obj.setPassword(json.getString("password"));
        }

        if (json.getValue("driverClassName") != null && json.getValue("driverClassName") instanceof String) {
            obj.setDriverClassName(json.getString("driverClassName"));
        }
    }
}
