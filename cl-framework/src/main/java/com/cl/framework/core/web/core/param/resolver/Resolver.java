package com.cl.framework.core.web.core.param.resolver;

import com.cl.framework.core.web.core.param.ParamContainer;
import io.vertx.rxjava3.ext.web.RoutingContext;


/**
 * 该接口用来解析 请求参数
 *
 * @author xhz
 */
public interface Resolver<T> {

    ParamContainer<T> resolve(RoutingContext context, ParamContainer<T> income);
}
