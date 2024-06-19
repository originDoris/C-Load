package com.cl.framework.core.codec;


import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.BytesUtils;
import com.cl.framework.core.utils.JsonUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/**
 * Codec to transfer envelop
 * @author xhz
 */
public final class EnvelopCodec implements MessageCodec<Envelop, Envelop> {

    @Override
    public void encodeToWire(final Buffer buffer,
                             final Envelop message) {
        buffer.appendBytes(BytesUtils.to(message));
    }

    @Override
    public Envelop decodeFromWire(final int i,
                                  final Buffer buffer) {
        return BytesUtils.from(i, buffer);
    }

    @Override
    public Envelop transform(final Envelop message) {
        return message;
    }

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
