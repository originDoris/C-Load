package com.cl.framework.core.web.core.route.axis;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.route.Hub;
import com.cl.framework.core.web.core.route.MediaHub;
import com.cl.framework.core.web.core.route.UriHub;
import com.cl.framework.core.web.core.Pool;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import io.vertx.rxjava3.ext.web.Route;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("all")
public class FilterAxis implements Axis<Router> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterAxis.class);

    private static final ConcurrentMap<String, Set<Event>> FILTERS = DataTableAnno.getFilters();

    @Override
    public void mount(final Router router) {
        FILTERS.forEach((path, events) -> events.forEach(event -> CubeFn.safeSemi(null == event,
                () -> LOGGER.warn(MessageFormat.format(MessageConstant.NULL_EVENT, this.getClass().getName())),
                () -> {
                    final Route route = router.route();

                    Hub<Route> hub = CubeFn.poolThread(Pool.URIHUBS, () -> ReflectionUtils.newInstance(UriHub.class));
                    hub.mount(route, event);
                    hub = CubeFn.poolThread(Pool.MEDIAHUBS, () -> ReflectionUtils.newInstance(MediaHub.class));
                    hub.mount(route, event);
                    route.handler(context -> {
                        final Method method = event.getAction();
                        final Object proxy = event.getProxy();
                        this.execute(context, proxy, method);
                    });
                }, LOGGER))
        );
    }

    private void execute(final RoutingContext context, final Object proxy, final Method method) {
        CubeFn.safeNull(() -> CubeFn.safeJvm(() -> {
            // Init context;
            ReflectionUtils.invoke(proxy, "init", context);
            // Extract Request/Response
            final HttpServerRequest request = context.request();
            final HttpServerResponse response = context.response();
            method.invoke(proxy, request, response);

            // Check whether called next or response
            if (!response.ended()) {
                context.next();
            }
        }, LOGGER), method, proxy);
    }
}
