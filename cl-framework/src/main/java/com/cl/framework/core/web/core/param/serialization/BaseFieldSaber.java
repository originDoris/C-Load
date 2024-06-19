package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.text.MessageFormat;

/**
 * BaseSaber
 *
 * @author xhz
 */
public abstract class BaseFieldSaber implements FieldSerializable {
    void verifyInput(final boolean condition,
                     final Class<?> paramType,
                     final String literal) {
        CubeFn.outError(getLogger(), condition, DataTableException.class,
                MessageFormat.format(ErrorInfoConstant.FIELD_SERIALIZABLE_ERROR, paramType, literal));
    }

    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public <T> Object from(final T input) {
        return input;
    }

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return literal;
    }
}
