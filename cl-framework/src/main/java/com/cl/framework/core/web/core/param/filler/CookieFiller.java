package com.cl.framework.core.web.core.param.filler;


import io.vertx.rxjava3.core.http.Cookie;
import io.vertx.rxjava3.ext.web.RoutingContext;

import java.util.Objects;


/**
 * @author xhz
 */
public class CookieFiller implements Filler {
    @Override
    public Object apply(final String name, final Class<?> paramType, final RoutingContext context) {
        if (Cookie.class.isAssignableFrom(paramType)) {
            return context.request().getCookie(name);
        } else {
            final Cookie cookie = context.request().getCookie(name);
            return Objects.isNull(cookie) ? null : cookie.getValue();
        }
    }
}
