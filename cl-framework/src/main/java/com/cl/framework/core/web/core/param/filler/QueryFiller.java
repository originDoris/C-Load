package com.cl.framework.core.web.core.param.filler;

import com.cl.framework.core.runtime.DataTableSerializer;
import io.vertx.rxjava3.core.MultiMap;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * 处理@QueryParam
 * @author xhz
 */
public class QueryFiller implements Filler {

    @Override
    public Object apply(final String name, final Class<?> paramType, final RoutingContext context) {
        MultiMap entries = context.queryParams();
        return DataTableSerializer.getValue(paramType, entries.get(name));
    }
}
