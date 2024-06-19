package com.cl.framework.core.web.core.filter;


import io.vertx.core.VertxException;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import io.vertx.rxjava3.ext.web.RoutingContext;

import java.io.IOException;

/**
 * 过滤器接口，必须是该类的子类 并且添加了@WebFilter注解才会被系统加载
 *
 * @author xhz
 */
public interface Filter {
    void doFilter(final HttpServerRequest request,
                  final HttpServerResponse response)
            throws IOException, VertxException;

    default void init(final RoutingContext context) {
    }
}
