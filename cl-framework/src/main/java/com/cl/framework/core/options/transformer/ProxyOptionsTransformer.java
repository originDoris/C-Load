package com.cl.framework.core.options.transformer;

import com.cl.framework.core.options.ProxyOptions;
import io.vertx.core.json.JsonObject;

/**
 * ProxyOptionsTransformer
 *
 * @author xhz
 */
public class ProxyOptionsTransformer implements Transformer<ProxyOptions> {

    @Override
    public ProxyOptions transform(JsonObject input) {
        return null == input ? new ProxyOptions() : new ProxyOptions(input);
    }
}
