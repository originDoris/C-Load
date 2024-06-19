package com.cl.framework.core.options;

import com.cl.framework.core.options.converter.ClusterOptionsConverter;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;


/**
 * @Description: vertx 集群配置
 * @author xhz
 */
@DataObject(generateConverter = true, publicConverter = false)
public class HazelcastClusterOptions implements Serializable {

    private static final boolean ENABLED = false;


    public static final String CLUSTER_NAME = "dev";

    private boolean enabled;

    private String clusterName;

    private String instanceName;

    public HazelcastClusterOptions() {
        this.enabled = ENABLED;
        this.clusterName = CLUSTER_NAME;
    }

    public HazelcastClusterOptions(final HazelcastClusterOptions other) {
        this.enabled = other.isEnabled();
        this.clusterName = other.getClusterName();
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public HazelcastClusterOptions(final JsonObject json) {
        this();
        ClusterOptionsConverter.fromJson(json, this);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    @Fluent
    public HazelcastClusterOptions setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
