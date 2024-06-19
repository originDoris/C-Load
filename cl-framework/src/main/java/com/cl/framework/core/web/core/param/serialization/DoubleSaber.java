package com.cl.framework.core.web.core.param.serialization;

import java.util.function.Function;

/**
 * Double type
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class DoubleSaber extends DecimalSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return double.class == paramType || Double.class == paramType;
    }

    @Override
    protected Function<String, Double> getFun() {
        return Double::parseDouble;
    }
}
