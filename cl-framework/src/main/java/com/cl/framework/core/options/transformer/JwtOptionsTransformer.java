package com.cl.framework.core.options.transformer;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;

/**
 * JwtOptionsTransformer
 *
 * @author xhz
 */
public class JwtOptionsTransformer implements Transformer<JWTOptions> {
    @Override
    public JWTOptions transform(JsonObject input) {
        return null == input ? new JWTOptions() : new JWTOptions(input);
    }
}
