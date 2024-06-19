package com.cl.framework.core.utils;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.Envelop;
import io.vertx.core.buffer.Buffer;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 字节处理工具类
 *
 * @author xhz
 */
public class BytesUtils {


    public static <T> byte[] to(final T message) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        return CubeFn.safeDefault(new byte[0], () -> {
            bytes.write(JsonUtil.toJson(message).getBytes(StandardCharsets.UTF_8));
            return bytes.toByteArray();
        }, bytes);
    }

    public static Envelop from(final int pos, final Buffer buffer) {
        String substring = buffer.toString().substring(pos);
        return JsonUtil.toObject(JsonUtil.stringToJsonObject(substring), Envelop.class);
    }
}
