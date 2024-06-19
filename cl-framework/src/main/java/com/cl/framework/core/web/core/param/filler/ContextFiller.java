package com.cl.framework.core.web.core.param.filler;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.DataTableSerializer;
import io.vertx.rxjava3.ext.web.RoutingContext;


import java.util.Map;

/**
 * @author xhz
 */
public class ContextFiller implements Filler {
    @Override
    public Object apply(final String name, final Class<?> paramType, final RoutingContext context) {
        final Map<String, Object> data = context.data();
        final Object value = data.get(name);
        return CubeFn.getDefault(null, () -> {
            if (paramType == value.getClass()) {
                return value;
            } else {
                final String valueStr = value.toString();
                return DataTableSerializer.getValue(paramType, valueStr);
            }
        }, value);
    }
}
