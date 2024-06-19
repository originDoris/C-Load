package com.cl.framework.core.web.core.agent;

import com.cl.framework.core.annotation.Agent;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.vertx.VertxLauncher;
import com.cl.framework.core.web.core.route.axis.*;
import com.cl.framework.core.web.core.Pool;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.rxjava3.ext.web.Route;
import io.vertx.rxjava3.ext.web.Router;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * 默认HttpAgent 建议不要替换
 *
 * @author xhz
 */
@Agent
public class DataTableHttpAgent extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTableHttpAgent.class);


    @Override
    public void start() {
        Axis<Router> routerAxis = CubeFn.poolThread(Pool.ROUTERS, () -> new RouterAxis(VertxLauncher.getVertx()));

        // 自定义路由
        final Axis<Router> axiser = CubeFn.poolThread(Pool.EVENTS, EventAxis::new);

        // 过滤器
        final Axis<Router> filterAxiser = CubeFn.poolThread(Pool.FILTERS, FilterAxis::new);

        final Axis<Router> wallAxiser = CubeFn.poolThread(Pool.WALLS, () -> ReflectionUtils.newInstance(WallAxis.class, this.vertx));


        // 读取http服务配置
        ConcurrentMap<Integer, HttpServerOptions> httpServerOpts = com.cl.framework.core.runtime.DataTableConfig.getHttpServerOpts();
        httpServerOpts.forEach((port, httpServerOptions) -> {
            final HttpServer server = this.vertx.createHttpServer(httpServerOptions);
            final Router router = Router.router(this.vertx);
            // router
            routerAxis.mount(router);
            // Event
            axiser.mount(router);
            // Filter
            filterAxiser.mount(router);
            wallAxiser.mount(router);

            server.requestHandler(router).listen().subscribe();
            this.registryServer(httpServerOptions, router);

        });
    }

    @Override
    public void stop() {
    }

    private void registryServer(final HttpServerOptions options,
                                final Router router) {
        final Integer port = options.getPort();
        String host = options.getHost();

        final String portLiteral = String.valueOf(port);
        LOGGER.info(MessageFormat.format(MessageConstant.HTTP_SERVERS, this.getClass().getSimpleName(), this.deploymentID(), portLiteral));
        final List<Route> routes = router.getRoutes();
        final Map<String, Set<Route>> routeMap = new TreeMap<>();

        final Set<String> tree = new TreeSet<>();
        for (final Route route : routes) {
            if (null != route.getPath()) {
                if (!routeMap.containsKey(route.getPath())) {
                    routeMap.put(route.getPath(), new HashSet<>());
                }
                routeMap.get(route.getPath()).add(route);
            }
            final String path = null == route.getPath() ? "/*" : route.getPath();
            if (!"/*".equals(path)) {
                tree.add(path);
            }
        }
        routeMap.forEach((path, routeSet) -> routeSet.forEach(route -> LOGGER.info(MessageFormat.format(MessageConstant.MAPPED_ROUTE, this.getClass().getSimpleName(), path, route.toString()))));
        final String address = MessageFormat.format("http://{0}:{1}/", host, portLiteral);
        LOGGER.info(MessageFormat.format(MessageConstant.HTTP_LISTEN, this.getClass().getSimpleName(), address));
    }

}
