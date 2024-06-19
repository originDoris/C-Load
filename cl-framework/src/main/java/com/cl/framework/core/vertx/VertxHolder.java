package com.cl.framework.core.vertx;

import io.vertx.core.VertxOptions;
import io.vertx.rxjava3.core.Vertx;

import java.util.function.Consumer;

/**
 * 描述：Vertx全局对象持有者（非集群）
 * @author xhz
 */
public class VertxHolder extends VertxLauncher{
    private static Vertx instance;
    public VertxHolder(){
    }

    @Override
    public void start(Consumer<Vertx> startConsumer, VertxOptions options) {
        if (instance == null) {
            instance = Vertx.vertx(options);
        }
        startConsumer.accept(instance);
    }

    @Override
    protected Vertx vertx() {
        return instance;
    }
}
