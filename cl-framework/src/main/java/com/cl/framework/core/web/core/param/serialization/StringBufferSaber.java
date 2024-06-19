package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;


/**
 * StringBuffer, StringBuilder
 * @author xhz
 */
public class StringBufferSaber extends BaseFieldSaber {
    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return CubeFn.getDefault(null, () ->
                        CubeFn.getSemi(StringBuilder.class == paramType
                                        || StringBuffer.class == paramType, getLogger(),
                                () -> {
                                    if (StringBuffer.class == paramType) {
                                        return new StringBuffer(literal);
                                    } else {
                                        return new StringBuilder(literal);
                                    }
                                }, () -> ""),
                paramType, literal);
    }

    @Override
    public <T> Object from(final T input) {
        return CubeFn.getDefault(null, () -> {
            Object reference = null;
            if (input instanceof StringBuilder
                    || input instanceof StringBuffer) {
                reference = input.toString();
            }
            return reference;
        }, input);
    }
}
