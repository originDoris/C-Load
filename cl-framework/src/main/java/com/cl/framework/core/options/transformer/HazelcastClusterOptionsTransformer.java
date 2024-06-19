package com.cl.framework.core.options.transformer;

import com.cl.framework.core.options.HazelcastClusterOptions;
import io.vertx.core.json.JsonObject;

/**
 * vertx集群配置
 *
 * @author xhz
 */
public class HazelcastClusterOptionsTransformer implements Transformer<HazelcastClusterOptions> {

    @Override
    public HazelcastClusterOptions transform(JsonObject input) {
        return null == input ? new HazelcastClusterOptions() : new HazelcastClusterOptions(input);
    }
}
