package com.cl.framework.core.web.core.param.serialization;

import java.util.function.Function;

/**
 * Integer type
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class IntegerSaber extends NumericSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return int.class == paramType || Integer.class == paramType;
    }

    @Override
    protected Function<String, Integer> getFun() {
        return Integer::parseInt;
    }
}
