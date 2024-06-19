package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.HazelcastClusterOptions;
import com.cl.framework.core.options.transformer.HazelcastClusterOptionsTransformer;
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
public class HazelcastOptionsVisitor implements ConfigVisitor<HazelcastClusterOptions> {

    public static final String KEY_CLUSTERED = "hazelcast";

    private transient final Transformer<HazelcastClusterOptions> clusterOptionsTransformer = ReflectionUtils.singleton(HazelcastClusterOptionsTransformer.class);

    @Override
    public HazelcastClusterOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY_CLUSTERED);
        log.info("加载配置文件前缀：{}, name:{},data:{}", KEY_CLUSTERED, this.getClass().getSimpleName(), config);
        return this.clusterOptionsTransformer.transform(config);
    }
}
