package com.cl.framework.core.web.core.param.serialization;

import java.util.function.Function;

/**
 * Long type
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class LongSaber extends NumericSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return long.class == paramType || Long.class == paramType;
    }

    @Override
    protected Function<String, Long> getFun() {
        return Long::parseLong;
    }
}
