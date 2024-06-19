package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.options.transformer.VertxOptionsTransformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * vertx 配置文件读取
 *
 * @author xhz
 */
@Slf4j
public class VertxConfigOptionsVisitor implements ConfigVisitor<VertxOptions> {

    private static final String KEY = "vertx";

    private transient final Transformer<VertxOptions> vertxOptionsTransformer = ReflectionUtils.singleton(VertxOptionsTransformer.class);

    @Override
    public VertxOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY);
        log.info("加载配置文件前缀：{}, name:{},data:{}", KEY, this.getClass().getSimpleName(), config);
        return vertxOptionsTransformer.transform(config);
    }
}
