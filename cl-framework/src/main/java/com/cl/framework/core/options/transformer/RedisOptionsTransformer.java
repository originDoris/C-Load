package com.cl.framework.core.options.transformer;

import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.RedisOptions;

/**
 * RedisOptionsTransformer
 *
 * @author xhz
 */
public class RedisOptionsTransformer implements Transformer<RedisOptions> {

    @Override
    public RedisOptions transform(JsonObject input) {
        return null == input ? new RedisOptions() : new RedisOptions(input);
    }
}
