package com.cl.framework.core.web.core.route.parse;


import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.web.core.param.ParamContainer;
import com.cl.framework.core.web.core.param.filler.Filler;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class StandardAtomic<T> implements Atomic<T> {

    @Override
    public ParamContainer<T> ingest(final RoutingContext context,
                                    final ParamContainer<T> income) throws WebException {
        final Filler filler = Filler.PARAMS.get(income.getAnnotation().annotationType());
        final Object value = filler.apply(income.getName(), income.getArgType(), context);
        if (null == value) {
            income.setValue(null);
        } else {
            income.setValue((T) value);
        }
        return income;
    }
}
