package com.cl.framework.core.options.transformer;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.PoolOptions;

/**
 * PoolOptions 转换器
 *
 * @author xhz
 */
public class PgPoolOptionsTransformer implements Transformer<PoolOptions>{
    @Override
    public PoolOptions transform(JsonObject input) {
        return null == input ? new PoolOptions() : new PoolOptions(input);
    }
}
