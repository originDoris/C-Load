package com.cl.framework.core.web.core.param.filler;

import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.Session;

/**
 * @author xhz
 */
public class SessionFiller implements Filler {
    @Override
    public Object apply(final String name, final Class<?> paramType, final RoutingContext context) {
        Session session = context.session();
        return session.get(name);
    }
}
