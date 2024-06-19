package com.cl.framework.plugin.jooq.database;

import com.cl.framework.core.vertx.VertxHolder;

import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.SqlClient;
import io.vertx.sqlclient.PoolOptions;


/**
 * ReactiveDatabaseClientProvider
 * @author xhz
 */
public class ReactiveDatabaseClientProvider {

    public static int POOL_SIZE = 8;
    private static ReactiveDatabaseClientProvider INSTANCE;
    public static ReactiveDatabaseClientProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new ReactiveDatabaseClientProvider() : INSTANCE;
    }

    private final PgPool pgClient;

    private ReactiveDatabaseClientProvider() {
        this.pgClient = PgPool.pool(VertxHolder.getVertx(), com.cl.framework.core.runtime.DataTableConfig.getPgOptions(), com.cl.framework.core.runtime.DataTableConfig.getPoolOptions() == null ? new PoolOptions().setMaxSize(POOL_SIZE) : com.cl.framework.core.runtime.DataTableConfig.getPoolOptions());
    }

    public SqlClient getClient() {
        return pgClient;
    }




}
