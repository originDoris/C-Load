package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.FieldUtil;
/**
 * Boolean
 * @author xhz
 */
public class BooleanSaber extends BaseFieldSaber {



    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return CubeFn.getSemi(boolean.class == paramType || Boolean.class == paramType, getLogger(),
                () -> {
                    verifyInput(!FieldUtil.isBoolean(literal), paramType, literal);
                    return Boolean.parseBoolean(literal);
                }, () -> Boolean.FALSE);
    }
}
