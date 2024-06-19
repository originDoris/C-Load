package com.cl.framework.core.web.core.secure;


import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.ext.web.handler.AuthenticationHandler;

/**
 * 安全调度模块
 * 为不同的工作流 构建标准的AuthHandler
 * @author xhz
 */
public interface Bolt {


    AuthenticationHandler mount(final Vertx vertx,
                           final Cliff cliff);

    static Bolt get() {
        return ShuntBolt.create();
    }
}
