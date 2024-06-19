package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.transformer.PgConnectOptionsTransformer;
import com.cl.framework.core.options.transformer.PgPoolOptionsTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * PgConnectOptionsVisitor
 *
 * @author xhz
 */
@Slf4j
public class PgConnectOptionsVisitor implements ConfigVisitor<PgConnectOptions> {

    private static final String KEY = "pg";
    private static final String POOL_KEY = "pool";

    private transient final Transformer<PgConnectOptions> pgConnectOptionsTransformer = ReflectionUtils.singleton(PgConnectOptionsTransformer.class);
    private transient final Transformer<PoolOptions> pgPoolOptionsTransformer = ReflectionUtils.singleton(PgPoolOptionsTransformer.class);


    private transient PoolOptions poolOptions;

    @Override
    public PgConnectOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY);
        log.info("加载配置文件前缀：{}, name:{},data:{}", KEY, this.getClass().getSimpleName(), config);
        if (config != null) {
            poolOptions = pgPoolOptionsTransformer.transform(config.getJsonObject(POOL_KEY));
        }
        return pgConnectOptionsTransformer.transform(config);
    }


    public PoolOptions getPoolOptions() {
        return poolOptions;
    }
}
