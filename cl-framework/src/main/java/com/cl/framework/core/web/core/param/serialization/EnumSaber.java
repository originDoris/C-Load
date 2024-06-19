package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;

/**
 * Enum
 * @author xhz
 */
public class EnumSaber extends BaseFieldSaber {
    @Override
    public <T> Object from(final T input) {
        return CubeFn.getDefault(null, () -> {
            Object reference = null;
            if (input instanceof Enum) {
                reference = ReflectionUtils.invoke(input, "name");
            }
            return reference;
        }, input);
    }
}
