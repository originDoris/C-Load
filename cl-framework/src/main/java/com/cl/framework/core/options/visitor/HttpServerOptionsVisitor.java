package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.transformer.HttpServerOptionsTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * HttpServerOptionsVisitor
 *
 * @author xhz
 */
@Slf4j
public class HttpServerOptionsVisitor implements ConfigVisitor<HttpServerOptions> {

    private static final String KEY = "http";

    private transient final Transformer<HttpServerOptions> httpServerOptionsTransformer = ReflectionUtils.singleton(HttpServerOptionsTransformer.class);

    @Override

    public HttpServerOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY);
        log.info("加载配置文件前缀：{}, name:{},data:{}", KEY, this.getClass().getSimpleName(), config);
        return httpServerOptionsTransformer.transform(config);
    }

}
