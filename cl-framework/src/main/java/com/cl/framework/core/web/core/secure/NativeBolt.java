package com.cl.framework.core.web.core.secure;


import com.cl.framework.core.funcation.CubeFn;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.ext.web.handler.AuthenticationHandler;

import java.lang.reflect.InvocationTargetException;

/**
 * 绑定AuthenticationHandler
 * @Author: xhz
 */
class NativeBolt implements Bolt {

    private static Bolt INSTANCE;

    static Bolt create() {
        if (null == INSTANCE) {
            INSTANCE = new NativeBolt();
        }
        return INSTANCE;
    }

    @Override
    public AuthenticationHandler mount(final Vertx vertx,
                                       final Cliff cliff) {
        return CubeFn.getDefault(null,() -> {
                    final JsonObject config = CubeFn.getNull(new JsonObject(), cliff::getConfig);
                    final Object reference;
                    try {
                        reference = cliff.getAuthorizer().getAuthenticate()
                                .invoke(cliff.getProxy(), vertx, config);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    return null == reference ? null : (AuthenticationHandler) reference;
                }, cliff, cliff.getProxy(), cliff.getAuthorizer(),
                cliff.getAuthorizer().getAuthenticate());
    }
}
