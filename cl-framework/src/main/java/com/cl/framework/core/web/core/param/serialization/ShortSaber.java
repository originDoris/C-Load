package com.cl.framework.core.web.core.param.serialization;

import java.util.function.Function;

/**
 * Short type
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class ShortSaber extends NumericSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return short.class == paramType || Short.class == paramType;
    }

    @Override
    protected Function<String, Short> getFun() {
        return Short::parseShort;
    }
}
