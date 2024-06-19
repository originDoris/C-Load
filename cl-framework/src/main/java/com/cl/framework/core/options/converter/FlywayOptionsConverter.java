package com.cl.framework.core.options.converter;

import com.cl.framework.core.options.FlywayOptions;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Objects;

/**
 * FlywayOptionsConverter
 * @author xhz
 */
public final class FlywayOptionsConverter {

    public static final String PREFIX = "flyway.";

    private FlywayOptionsConverter() {
    }
//        private boolean cleanDisabled;
//
//    private Integer baselineVersion;
//
//    private boolean validateOnMigrate;

    public static void fromJson(final JsonObject json, final FlywayOptions obj) {
        if (json.getValue("url") != null && json.getValue("url") instanceof String) {
            obj.setUrl(json.getString("url"));
        }
        if (json.getValue("user") != null && json.getValue("user") instanceof String) {
            obj.setUser(json.getString("user"));
        }
        if (json.getValue("password") != null && json.getValue("password") instanceof String) {
            obj.setPassword(json.getString("password"));
        }
        if (json.getValue("locations") != null && json.getValue("locations") instanceof String) {
            obj.setLocations(json.getString("locations"));
        }
        if (json.getValue("enable") != null && json.getValue("enable") instanceof Boolean) {
            obj.setEnable(json.getBoolean("enable"));
        }
        if (json.getValue("table") != null && json.getValue("table") instanceof String) {
            obj.setTable(json.getString("table"));
        }
        if (json.getValue("baselineOnMigrate") != null && json.getValue("baselineOnMigrate") instanceof Boolean) {
            obj.setBaselineOnMigrate(json.getBoolean("baselineOnMigrate").toString());
        }
        if (json.getValue("baselineVersion") != null && json.getValue("baselineVersion") instanceof Number) {
            obj.setBaselineVersion(json.getInteger("baselineVersion"));
        }
        if (json.getValue("cleanDisabled") != null && json.getValue("cleanDisabled") instanceof Boolean) {
            obj.setCleanDisabled(json.getBoolean("cleanDisabled"));
        }
        if (json.getValue("validateOnMigrate") != null && json.getValue("validateOnMigrate") instanceof Boolean) {
            obj.setValidateOnMigrate(json.getBoolean("validateOnMigrate"));
        }
    }


    public static HashMap<String, String> fromMap(final FlywayOptions obj) {
        HashMap<String, String> result = new HashMap<>();
        if (StringUtils.isNotBlank(obj.getUrl())) {
            result.put(PREFIX + "url", obj.getUrl());
        }
        if (StringUtils.isNotBlank(obj.getUser())) {
            result.put(PREFIX + "user", obj.getUser());
        }
        if (StringUtils.isNotBlank(obj.getPassword())) {
            result.put(PREFIX + "password", obj.getPassword());
        }
        if (StringUtils.isNotBlank(obj.getLocations())) {
            result.put(PREFIX + "locations", obj.getLocations());
        }
        if (StringUtils.isNotBlank(obj.getBaselineOnMigrate())) {
            result.put(PREFIX + "baselineOnMigrate", obj.getBaselineOnMigrate());
        }
        if (StringUtils.isNotBlank(obj.getTable())) {
            result.put(PREFIX + "table", obj.getTable());
        }
        if (obj.getCleanDisabled() != null) {
            result.put(PREFIX + "cleanDisabled", obj.getCleanDisabled().toString());
        }
        if (obj.getBaselineVersion() != null) {
            result.put(PREFIX + "baselineVersion", obj.getBaselineVersion().toString());
        }
        if (obj.getValidateOnMigrate() != null) {
            result.put(PREFIX + "validateOnMigrate", obj.getValidateOnMigrate().toString());
        }
        return result;
    }
}
