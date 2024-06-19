package com.cl.framework.core.web.core.route;

import com.cl.framework.core.web.core.agent.Event;
import io.vertx.rxjava3.ext.web.Route;

/**
 * path, method, order
 * @author xhz
 */
public class UriHub implements Hub<Route> {

    @Override
    public void mount(final Route route, final Event event) {
        if (null == event.getMethod()) {
            route.path(event.getPath()).order(event.getOrder());
        } else {
            route.path(event.getPath()).method(event.getMethod()).order(event.getOrder());
        }
    }
}
