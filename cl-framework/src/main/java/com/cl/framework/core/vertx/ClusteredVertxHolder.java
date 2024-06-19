package com.cl.framework.core.vertx;

import com.hazelcast.config.Config;
import io.vertx.core.VertxOptions;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.function.Consumer;

/**
 * 描述：Vertx全局对象持有者（集群）
 * @author xhz
 */
public class ClusteredVertxHolder extends VertxLauncher {
    private static Vertx instance;
    public ClusteredVertxHolder() {
    }

    @Override
    public void start(Consumer<Vertx> startConsumer, VertxOptions options) {
        if (instance == null) {
            Config config = new Config();
            config.setInstanceName(com.cl.framework.core.runtime.DataTableConfig.getCluster().getInstanceName());
            config.setClusterName(com.cl.framework.core.runtime.DataTableConfig.getCluster().getClusterName());
            HazelcastClusterManager mgr = new HazelcastClusterManager();
            mgr.setConfig(config);
            options.setClusterManager(mgr);
            instance = Vertx.rxClusteredVertx(options).blockingGet();
        }
        startConsumer.accept(instance);
    }

    @Override
    protected Vertx vertx() {
        return instance;
    }
}
