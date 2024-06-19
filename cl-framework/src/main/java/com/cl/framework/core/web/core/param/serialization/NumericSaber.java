package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * Int, Long, Short
 * @author xhz
 */
public abstract class NumericSaber extends BaseFieldSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return CubeFn.getDefault(null, () ->
                        CubeFn.getSemi(isValid(paramType), getLogger(),
                                () -> {
                                    verifyInput(StringUtils.isNotBlank(literal) && !FieldUtil.isInteger(literal), paramType, literal);
                                    return getFun().apply(literal);
                                }, () -> null),
                paramType, literal);
    }

    protected abstract boolean isValid(final Class<?> paramType);

    protected abstract <T> Function<String, T> getFun();
}
