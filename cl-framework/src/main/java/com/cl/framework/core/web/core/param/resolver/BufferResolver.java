package com.cl.framework.core.web.core.param.resolver;

import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.web.core.param.ParamContainer;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * buffer 转换
 * @author xhz
 */
public class BufferResolver<T> implements Resolver<T> {

    @Override
    @SuppressWarnings("all")
    public ParamContainer<T> resolve(final RoutingContext context, final ParamContainer<T> income) throws WebException {
        final Class<?> clazz = income.getArgType();
        if (Buffer.class == clazz) {
            final Buffer body = context.getBody();
            income.setValue((T) body);
        }
        return income;
    }
}
