package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.FlywayOptions;
import com.cl.framework.core.options.transformer.FlywayOptionsTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 表达式解析器
 *
 * @author xhz
 */
@Slf4j
public class FlywayOptionsVisitor implements ConfigVisitor<FlywayOptions>{
    public static final String KEY = "flyway";

    private transient final Transformer<FlywayOptions> flywayOptionsTransformer = ReflectionUtils.singleton(FlywayOptionsTransformer.class);
    @Override
    public FlywayOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY);
        log.info("加载配置文件前缀：{}, name:{},data:{}", KEY, this.getClass().getSimpleName(), config);
        return this.flywayOptionsTransformer.transform(config);
    }
}
