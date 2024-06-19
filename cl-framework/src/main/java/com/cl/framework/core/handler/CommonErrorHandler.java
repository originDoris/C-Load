package com.cl.framework.core.handler;

import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * DataTable 通用异常捕获
 *
 * @author xhz
 */
public class CommonErrorHandler implements Handler<RoutingContext> {
    private CommonErrorHandler() {
    }

    public static Handler<RoutingContext> create() {
        return new CommonErrorHandler();
    }

    @Override
    public void handle(final RoutingContext event) {
        if (event.failed()) {
            final Throwable error = event.failure();
            error.printStackTrace();
        }
    }
}
