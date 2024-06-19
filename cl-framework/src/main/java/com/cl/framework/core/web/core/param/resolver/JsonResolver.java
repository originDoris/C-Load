package com.cl.framework.core.web.core.param.resolver;

import com.cl.framework.core.runtime.DataTableSerializer;
import com.cl.framework.core.web.core.param.ParamContainer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * JsonResolver
 *
 * @author xhz
 */
public class JsonResolver<T> implements Resolver<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonResolver.class);

    @Override
    public ParamContainer<T> resolve(RoutingContext context, ParamContainer<T> income) {
        final String content = context.getBodyAsString();
        LOGGER.info(MessageFormat.format("( Resolver ) Income Type: {0}, Content = {1}", income.getArgType().getName(), content));

        if (StringUtils.isBlank(content)) {
            final T defaultValue = (T) income.getDefaultValue();
            income.setValue(defaultValue);
        } else {
            final Object result = DataTableSerializer.getValue(income.getArgType(), content);
            if (null != result) {
                income.setValue((T) result);
            }
        }
        return income;
    }
}
