package com.cl.framework.core.web.core.route.parse;

import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.web.core.param.ParamContainer;
import com.cl.framework.core.web.core.param.serialization.TypedArgument;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class TypedAtomic<T> implements Atomic<T> {
    @Override
    public ParamContainer<T> ingest(final RoutingContext context,
                                    final ParamContainer<T> income)
            throws WebException {
        final Class<?> paramType = income.getArgType();
        final Object returnValue = TypedArgument.analyze(context, paramType);
        if (null == returnValue) {
            income.setValue(null);
        }else{
            income.setValue((T) returnValue);
        }
        return income;
    }
}
