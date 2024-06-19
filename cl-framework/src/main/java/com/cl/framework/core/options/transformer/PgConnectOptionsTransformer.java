package com.cl.framework.core.options.transformer;

import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;

/**
 * pgConnectOptions 转换器
 *
 * @author xhz
 */
public class PgConnectOptionsTransformer implements Transformer<PgConnectOptions>{
    @Override
    public PgConnectOptions transform(JsonObject input) {
        return null == input ? new PgConnectOptions() : new PgConnectOptions(input);
    }
}
