package com.cl.framework.core.web.core.route.parse;

import com.cl.framework.core.web.core.agent.Event;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 *
 * 解析Http请求的MIME信息
 *
 * @author xhz
 */
public interface Income<T> {

    T in(RoutingContext context, Event event);
}
