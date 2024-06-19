package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;

import java.nio.charset.StandardCharsets;

/**
 * ByteArray
 * @author xhz
 */
public class ByteArraySaber extends BaseFieldSaber {

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return CubeFn.getDefault(null,() -> CubeFn.getSemi(Byte[].class == paramType ||
                        byte[].class == paramType, getLogger(),
                () -> literal.getBytes(StandardCharsets.UTF_8), () -> new byte[0]),
                paramType, literal);
    }
}
