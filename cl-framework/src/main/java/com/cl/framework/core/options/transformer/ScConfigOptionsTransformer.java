package com.cl.framework.core.options.transformer;

import com.cl.framework.core.options.ScConfigOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.RedisOptions;

/**
 * ScConfigOptionsTransformer
 *
 * @author xhz
 */
public class ScConfigOptionsTransformer implements Transformer<ScConfigOptions> {

    @Override
    public ScConfigOptions transform(JsonObject input) {
        return null == input ? new ScConfigOptions() : new ScConfigOptions(input);
    }
}
