package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;
import io.vertx.core.buffer.Buffer;
import java.nio.charset.StandardCharsets;

/**
 * Buffer
 * @author xhz
 */
public class BufferSaber extends BaseFieldSaber {
    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return CubeFn.getDefault(null, () ->
                        CubeFn.getSemi(Buffer.class == paramType, getLogger(),
                                () -> {
                                    final Buffer buffer = Buffer.buffer();
                                    buffer.appendBytes(literal.getBytes(StandardCharsets.UTF_8));
                                    return buffer;
                                }, Buffer::buffer),
                paramType, literal);
    }
}
