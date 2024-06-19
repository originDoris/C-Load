package com.cl.framework.core.web.core.param.filler;

import com.cl.framework.core.runtime.DataTableSerializer;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * 处理 @HeaderParam
 * @author xhz
 */
public class HeaderFiller implements Filler {
    @Override
    public Object apply(final String name, final Class<?> paramType, final RoutingContext context) {
        HttpServerRequest request = context.request();
        return DataTableSerializer.getValue(paramType, request.getHeader(name));
    }
}
