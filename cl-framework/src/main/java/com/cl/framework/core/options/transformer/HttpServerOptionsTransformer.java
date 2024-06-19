package com.cl.framework.core.options.transformer;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

/**
 * PoolOptions 转换器
 *
 * @author xhz
 */
public class HttpServerOptionsTransformer implements Transformer<HttpServerOptions>{
    @Override
    public HttpServerOptions transform(JsonObject input) {
        return null == input ? new HttpServerOptions() : new HttpServerOptions(input);
    }
}
