package com.cl.framework.core.web.core.param.resolver;


import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.web.core.param.ParamContainer;
import io.vertx.rxjava3.ext.web.RoutingContext;


/**
 * @author xhz
 */
public class DefaultResolver<T> implements Resolver<T> {

    @Override
    public ParamContainer<T> resolve(final RoutingContext context,
                                     final ParamContainer<T> income) throws WebException {
        return income;
    }
}
