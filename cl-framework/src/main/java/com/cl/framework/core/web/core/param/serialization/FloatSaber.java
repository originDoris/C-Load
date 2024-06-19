package com.cl.framework.core.web.core.param.serialization;

import java.util.function.Function;

/**
 * Float type
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class FloatSaber extends DecimalSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return float.class == paramType || Float.class == paramType;
    }

    @Override
    protected Function<String, Float> getFun() {
        return Float::parseFloat;
    }
}
