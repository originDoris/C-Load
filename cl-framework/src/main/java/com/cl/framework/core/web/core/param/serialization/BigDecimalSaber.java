package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * BigDecimal
 * @author xhz
 */
@SuppressWarnings("unchecked")
public class BigDecimalSaber extends DecimalSaber {
    @Override
    protected boolean isValid(final Class<?> paramType) {
        return BigDecimal.class == paramType;
    }

    @Override
    protected Function<String, BigDecimal> getFun() {
        return BigDecimal::new;
    }

    @Override
    public <T> Object from(final T input) {
        return CubeFn.getDefault(null, () -> {
            final BigDecimal decimal = (BigDecimal) input;
            return decimal.doubleValue();
        }, input);
    }
}
