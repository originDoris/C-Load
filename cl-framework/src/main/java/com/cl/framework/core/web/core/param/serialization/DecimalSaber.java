package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.FieldUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.function.Function;

/**
 * Double, Float, BigDecimal
 * @author xhz
 */
public abstract class DecimalSaber extends BaseFieldSaber {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecimalSaber.class);

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return CubeFn.getDefault(null, () ->
                CubeFn.getSemi(isValid(paramType), LOGGER,
                        () -> {
                            verifyInput(!FieldUtil.isDecimal(literal), paramType, literal);
                            return getFun().apply(literal);
                        }, () -> 0.0), paramType, literal);
    }

    protected abstract boolean isValid(final Class<?> paramType);

    protected abstract <T> Function<String, T> getFun();
}
