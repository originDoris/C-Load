package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Hazelcast 集群管理器配置
 *
 * @author xhz
 */
@Slf4j
public class DataTableOptionsVisitor implements ConfigVisitor<com.cl.framework.core.options.DataTableOptions> {

    public static final String KEY = "DataTable";

    private transient final Transformer<com.cl.framework.core.options.DataTableOptions> dataCubeOptionsTransformer = ReflectionUtils.singleton(com.cl.framework.core.options.transformer.DataTableOptionsTransformer.class);

    @Override
    public com.cl.framework.core.options.DataTableOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY);
        log.info("加载配置文件前缀：{}, name:{},data:{}", KEY, this.getClass().getSimpleName(), config);
        return this.dataCubeOptionsTransformer.transform(config);
    }
}
