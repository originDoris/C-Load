package com.cl.framework.core.options.converter;

import com.cl.framework.core.options.HazelcastClusterOptions;
import com.cl.framework.core.utils.JsonUtil;
import com.hazelcast.config.Config;
import io.vertx.core.json.JsonObject;

/**
 * ClusterOptionsConverter
 * @author xhz
 */
public final class ClusterOptionsConverter {

    private ClusterOptionsConverter() {
    }

    public static void fromJson(final JsonObject json, final HazelcastClusterOptions obj) {
        if (json.getValue("enabled") != null && json.getValue("enabled") instanceof Boolean) {
            obj.setEnabled(json.getBoolean("enabled"));
        }
        if (json.getValue("clusterName") != null && json.getValue("clusterName") instanceof String) {
            obj.setClusterName(json.getString("clusterName"));
        }
    }
}
