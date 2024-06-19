package com.cl.framework.core.options.transformer;

import com.cl.framework.core.options.CorsConfigOptions;
import io.vertx.core.json.JsonObject;

/**
 * CorsConfigOptionsTransformer
 *
 * @author xhz
 */
public class CorsConfigOptionsTransformer implements Transformer<CorsConfigOptions>{
    @Override
    public CorsConfigOptions transform(JsonObject input) {
        return null == input ? new CorsConfigOptions() : new CorsConfigOptions(input);
    }
}
