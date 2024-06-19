package com.cl.framework.core.vertx;

import io.vertx.core.VertxOptions;
import io.vertx.rxjava3.core.Vertx;

import java.util.function.Consumer;

/**
 * 启动vertx 实例
 *
 * @author xhz
 */
public abstract class VertxLauncher {

    private static VertxLauncher LAUNCHER = null;

    abstract public void start(Consumer<Vertx> startConsumer, VertxOptions options);


    public static Vertx getVertx() {
        return LAUNCHER.vertx();
    }

    abstract protected Vertx vertx();


    public static VertxLauncher initLauncher() {
        if (LAUNCHER == null) {
            if (com.cl.framework.core.runtime.DataTableConfig.getCluster().isEnabled()) {
                LAUNCHER = new ClusteredVertxHolder();
            }else{
                LAUNCHER = new VertxHolder();
            }
        }

        return LAUNCHER;

    }
}
