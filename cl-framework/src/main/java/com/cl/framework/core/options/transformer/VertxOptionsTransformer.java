package com.cl.framework.core.options.transformer;

import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;


/**
 * @Description: VertxOptions
 * @author xhz
 */
@Slf4j
public class VertxOptionsTransformer implements Transformer<VertxOptions> {

    @Override
    public VertxOptions transform(final JsonObject input) {
        return null == input ? new VertxOptions() : new VertxOptions(input);
    }
}
