package com.cl.framework.core.web.core.param.filler;

import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * 什么都不做
 * @author xhz
 */
public class EmptyFiller implements Filler {
    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        return null;
    }
}
