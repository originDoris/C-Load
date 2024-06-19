package com.cl.framework.core.options.transformer;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

/**
 * JwtAuthOptionsTransformer
 *
 * @author xhz
 */
public class JwtAuthOptionsTransformer implements Transformer<JWTAuthOptions> {
    @Override
    public JWTAuthOptions transform(JsonObject input) {
        return null == input ? new JWTAuthOptions() : new JWTAuthOptions(input);
    }
}
