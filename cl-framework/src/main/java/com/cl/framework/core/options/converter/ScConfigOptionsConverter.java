package com.cl.framework.core.options.converter;

import com.cl.framework.core.options.HazelcastClusterOptions;
import com.cl.framework.core.options.ScConfigOptions;
import io.vertx.core.json.JsonObject;

/**
 * ScConfigOptionsConverter
 * @author xhz
 */
public final class ScConfigOptionsConverter {

    private ScConfigOptionsConverter() {
    }

    public static void fromJson(final JsonObject json, final ScConfigOptions obj) {


        if (json.getValue("codeExpired") != null && json.getValue("codeExpired") instanceof Integer) {
            obj.setCodeExpired(json.getInteger("codeExpired"));
        }
        if (json.getValue("codeLength") != null && json.getValue("codeLength") instanceof Integer) {
            obj.setCodeLength(json.getInteger("codeLength"));
        }

        if (json.getValue("codePool") != null && json.getValue("codePool") instanceof String) {
            obj.setCodePool(json.getString("codePool"));
        }

        if (json.getValue("tokenExpired") != null && json.getValue("tokenExpired") instanceof Long) {
            obj.setTokenExpired(json.getLong("tokenExpired"));
        }

        if (json.getValue("tokenPool") != null && json.getValue("tokenPool") instanceof String) {
            obj.setTokenPool(json.getString("tokenPool"));
        }

        if (json.getValue("supportGroup") != null && json.getValue("supportGroup") instanceof Boolean) {
            obj.setSupportGroup(json.getBoolean("supportGroup"));
        }

        if (json.getValue("supportSecondary") != null && json.getValue("supportSecondary") instanceof Boolean) {
            obj.setSupportSecondary(json.getBoolean("supportSecondary"));
        }

        if (json.getValue("supportMultiApp") != null && json.getValue("supportMultiApp") instanceof Boolean) {
            obj.setSupportMultiApp(json.getBoolean("supportMultiApp"));
        }
        if (json.getValue("permissionPool") != null && json.getValue("permissionPool") instanceof String) {
            obj.setPermissionPool(json.getString("permissionPool"));
        }
    }
}
