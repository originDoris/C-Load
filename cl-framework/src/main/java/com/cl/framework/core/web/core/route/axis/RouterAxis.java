package com.cl.framework.core.web.core.route.axis;

import com.cl.framework.core.constants.Orders;
import com.cl.framework.core.options.CorsConfigOptions;
import com.cl.framework.core.options.HazelcastClusterOptions;
import com.cl.framework.core.runtime.DataTableConfig;
import com.cl.framework.core.utils.JsonUtil;
import com.cl.framework.core.vertx.VertxLauncher;
import com.cl.framework.plugin.annotation.Redis;
import com.cl.framework.plugin.redis.RedisInfix;
import com.hazelcast.core.Hazelcast;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.redis.client.RedisOptions;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import io.vertx.rxjava3.ext.web.handler.CorsHandler;
import io.vertx.rxjava3.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.rxjava3.ext.web.handler.SessionHandler;
import io.vertx.rxjava3.ext.web.sstore.ClusteredSessionStore;
import io.vertx.rxjava3.ext.web.sstore.LocalSessionStore;
import io.vertx.rxjava3.ext.web.sstore.SessionStore;
import io.vertx.rxjava3.ext.web.sstore.redis.RedisSessionStore;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * RouterAxis
 *
 * @author xhz
 */
public class RouterAxis implements Axis<Router> {


    private static final Logger LOGGER = LoggerFactory.getLogger(RouterAxis.class);

    private static final int KB = 1024;
    private static final int MB = KB * 1024;

    private transient final Vertx vertx;

    public RouterAxis(final Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void mount(final Router router) {
        this.mountSession(router);
        router.route().order(Orders.BODY).handler(BodyHandler.create().setBodyLimit(32 * MB));
        router.route().order(Orders.CONTENT).handler(ResponseContentTypeHandler.create());
        this.mountCors(router);
    }

    private void mountSession(final Router router) {
        final SessionStore store;
        Boolean clusteredSession = DataTableConfig.getDataTableOptions().getClusteredSession();
        if (this.vertx.isClustered() && clusteredSession) {
            store = ClusteredSessionStore.create(this.vertx);
        }else {
            store = LocalSessionStore.create(this.vertx);
        }
        SessionHandler sessionHandler = SessionHandler.create(store);
        if (StringUtils.isNotBlank(DataTableConfig.getDataTableOptions().getSessionName())) {
            sessionHandler.setSessionCookieName(DataTableConfig.getDataTableOptions().getSessionName());
        }
        router.route().order(Orders.SESSION).handler(sessionHandler);

    }

    private void mountCors(final Router router) {
        CorsConfigOptions corsConfigOptions = DataTableConfig.getCorsConfigOptions();
        router.route().order(Orders.CORS).handler(CorsHandler.create()
                .allowCredentials(corsConfigOptions.getCredentials())
                .allowedHeaders(this.getAllowedHeaders(JsonUtil.toJsonArray(corsConfigOptions.getHeaders())))
                .allowedMethods(this.getAllowedMethods(JsonUtil.toJsonArray(corsConfigOptions.getMethods()))));
    }

    private Set<String> getAllowedHeaders(final JsonArray array) {
        final Set<String> headerSet = new HashSet<>();
        array.stream()
                .filter(Objects::nonNull)
                .map(item -> (String) item)
                .forEach(headerSet::add);
        return headerSet;
    }

    private Set<HttpMethod> getAllowedMethods(final JsonArray array) {
        final Set<HttpMethod> methodSet = new HashSet<>();
        array.stream()
                .filter(Objects::nonNull)
                .map(item -> (String) item)
                .map(item -> {
                    HttpMethod httpMethod = HttpMethod.valueOf(item);
                    if (httpMethod == null) {
                        return HttpMethod.GET;
                    }
                    return httpMethod;
                })
                .forEach(methodSet::add);
        return methodSet;
    }
}
