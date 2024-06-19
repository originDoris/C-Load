package com.cl.framework.core.web.core.param.resolver;

import com.cl.framework.core.web.core.param.ParamContainer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.ext.web.RoutingContext;


/**
 * @author xhz
 */
public class SolveResolver<T> implements Resolver<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolveResolver.class);

    @Override
    public ParamContainer<T> resolve(final RoutingContext context, final ParamContainer<T> income) {
        return income;
    }
}
