package com.cl.framework.core.web.core.secure;


import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.ext.web.handler.AuthenticationHandler;

/**
 * 负责分发不同的bolt
 * @Author: xhz
 */
class ShuntBolt implements Bolt {

    private static Bolt INSTANCE;

    static Bolt create() {
        if (null == INSTANCE) {
            INSTANCE = new ShuntBolt();
        }
        return INSTANCE;
    }

    @Override
    public AuthenticationHandler mount(final Vertx vertx,
                                       final Cliff cliff) {
        if (cliff.isDefined()) {
            return null;
        } else {
            final Bolt bolt = NativeBolt.create();
            return bolt.mount(vertx, cliff);
        }
    }
}
