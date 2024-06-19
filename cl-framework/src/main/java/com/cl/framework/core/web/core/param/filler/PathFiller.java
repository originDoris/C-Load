package com.cl.framework.core.web.core.param.filler;

import com.cl.framework.core.runtime.DataTableSerializer;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * 处理.@PathParam
 * @author xhz
 */
public class PathFiller implements Filler {
    @Override
    public Object apply(final String name, final Class<?> paramType, final RoutingContext context) {
        return DataTableSerializer.getValue(paramType, context.pathParam(name));
    }
}
