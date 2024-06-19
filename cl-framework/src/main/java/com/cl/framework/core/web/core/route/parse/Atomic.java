package com.cl.framework.core.web.core.route.parse;

import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.web.core.param.ParamContainer;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * 处理参数
 * @author xhz
 */
public interface Atomic<T> {

    ParamContainer<T> ingest(RoutingContext context,
                             ParamContainer<T> income) throws WebException;
}
