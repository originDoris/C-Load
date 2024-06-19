package com.cl.framework.plugin.jooq.database;

import com.cl.framework.core.exception.DataSourceException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.options.DataBaseOptions;
import com.cl.framework.core.utils.JsonUtil;
import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultConnectionProvider;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 使用HikariCP 实现数据库连接池
 * @author xhz
 */
public class HikariDataPool implements DataPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataPool.class);
    private static final String OPT_AUTO_COMMIT = "hikari.auto.commit";
    private static final String OPT_CONNECTION_TIMEOUT = "hikari.connection.timeout";
    private static final String OPT_IDLE_TIMEOUT = "hikari.idle.timeout";
    private static final String OPT_MAX_LIFETIME = "hikari.max.lifetime";
    private static final String OPT_MINIMUM_IDLE = "hikari.minimum.idle";
    private static final String OPT_MAXIMUM_POOL_SIZE = "hikari.maximum.pool.size";
    private static final String OPT_POOL_NAME = "hikari.name";
    private static final String OPT_STATEMENT_CACHED = "hikari.statement.cached";
    private static final String OPT_STATEMENT_CACHE_SIZE = "hikari.statement.cache.size";
    private static final String OPT_STATEMENT_CACHE_SQL_LIMIT = "hikari.statement.cache.sqllimit";
    private static final ConcurrentMap<String, DataPool> POOL_SWITCH = new ConcurrentHashMap<>();
    private final transient DataBaseOptions database;
    private transient DSLContext context;
    private transient HikariDataSource dataSource;

    HikariDataPool(final DataBaseOptions database) {
        this.database = database;
    }

    private void initDelay() {
        this.initJdbc();
        this.initPool();
        this.initJooq();
    }

    @Override
    public DataPool switchTo() {
        return CubeFn.pool(POOL_SWITCH, this.database.getJdbcUrl(), () -> {
            final DataBaseOptions database = new DataBaseOptions(JsonUtil.toJsonObject(this.database));

            // 关闭自动提交
            database.getOptions().remove(OPT_AUTO_COMMIT);
            final DataPool ds = new HikariDataPool(database);
            final Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.info(MessageFormat.format("[ DP ] Data Pool Hash : {0}", ds.hashCode()));
            return ds;
        });
    }

    @Override
    public DSLContext getExecutor() {
        if (Objects.isNull(this.context)) {
            this.initDelay();
        }
        return this.context;
    }

    @Override
    public HikariDataSource getDataSource() {
        if (Objects.isNull(this.dataSource)) {
            this.initDelay();
        }
        return this.dataSource;
    }

    @Override
    public DataBaseOptions getDatabase() {
        return this.database;
    }

    private void initJooq() {
        if (null == this.context) {
            try {
                final Configuration configuration = new DefaultConfiguration();
                final ConnectionProvider provider = new DefaultConnectionProvider(this.dataSource.getConnection());
                configuration.set(provider);
                final SQLDialect dialect = Pool.DIALECT.get(this.database.getCategory());
                HikariDataPool.LOGGER.debug(MessageFormat.format("Jooq Database ：Dialect = {0}, Database = {1}, ", dialect, JsonUtil.toJsonObject(this.database).encodePrettily()));
                configuration.set(dialect);
                this.context = DSL.using(configuration);
            } catch (final SQLException ex) {
                throw new DataSourceException(this.database.getJdbcUrl(), ex);
            }
        }
    }

    private void initJdbc() {
        if (null == this.dataSource) {
            this.dataSource = new HikariDataSource();
            this.dataSource.setJdbcUrl(this.database.getJdbcUrl());
            this.dataSource.setUsername(this.database.getUsername());
            this.dataSource.setPassword(this.database.getPassword());
            this.dataSource.setDriverClassName(this.database.getDriverClassName());
        }
    }

    private void initPool() {
        if (Objects.nonNull(this.database)) {
            final Boolean autoCommit = this.database.getOption(OPT_AUTO_COMMIT, Boolean.TRUE);

            this.dataSource.setAutoCommit(autoCommit);
            this.dataSource.setConnectionTimeout(this.database.getOption(OPT_CONNECTION_TIMEOUT, 300000L));
            this.dataSource.setIdleTimeout(this.database.getOption(OPT_IDLE_TIMEOUT, 600000L));
            this.dataSource.setMaxLifetime(this.database.getOption(OPT_MAX_LIFETIME, 25600000L));
            this.dataSource.setMinimumIdle(this.database.getOption(OPT_MINIMUM_IDLE, 50));
            this.dataSource.setMaximumPoolSize(this.database.getOption(OPT_MAXIMUM_POOL_SIZE, 8));

            this.dataSource.addDataSourceProperty("cachePrepStmts", this.database.getOption(OPT_STATEMENT_CACHED, "true"));
            this.dataSource.addDataSourceProperty("prepStmtCacheSize", this.database.getOption(OPT_STATEMENT_CACHE_SIZE, "2048"));
            this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", this.database.getOption(OPT_STATEMENT_CACHE_SQL_LIMIT, "4096"));

            this.dataSource.setPoolName(this.database.getOption(OPT_POOL_NAME, "VERTX-POOL-DATA"));
        }
    }
}
