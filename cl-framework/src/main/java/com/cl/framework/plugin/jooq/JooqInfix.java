package com.cl.framework.plugin.jooq;



import com.cl.framework.core.annotation.Plugin;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.options.DataBaseOptions;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.plugin.Infix;
import com.cl.framework.plugin.Plugins;
import com.cl.framework.plugin.exception.JooqConfigurationException;
import com.cl.framework.plugin.exception.JooqVertxNullException;
import com.cl.framework.plugin.jooq.database.DataPool;
import com.cl.framework.plugin.jooq.database.ReactiveDatabaseClientProvider;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.sqlclient.SqlClient;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.meta.Database;


import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * JOOQ 插件
 * @author xhz
 */
@Plugin
@SuppressWarnings("unchecked")
public class JooqInfix implements Infix {

    private static final Logger LOGGER = LoggerFactory.getLogger(JooqInfix.class);
    private static final ConcurrentMap<String, Configuration> CONFIGURATION = new ConcurrentHashMap<>();
    private static Vertx vertxRef;

    public static void init(final Vertx vertx) {
        vertxRef = vertx;
        final ConcurrentMap<String, Configuration> inited = Infix.init(Plugins.Infix.JOOQ, JooqPin::initConfiguration, JooqInfix.class);
        CONFIGURATION.putAll(inited);
    }

    public static <T> T getDao(final Class<T> clazz) {
        return getDao(clazz, "provider");
    }

    public static <T> T getDao(final Class<T> clazz, final String key) {
        return getDao(clazz, CONFIGURATION.get(key));
    }

    public static <T> T getDao(final Class<T> clazz, final DataPool pool) {
        final DataBaseOptions database = pool.getDatabase();
        final Configuration configuration;
        if (CONFIGURATION.containsKey(database.getJdbcUrl())) {
            configuration = CONFIGURATION.get(database.getJdbcUrl());
        } else {
            configuration = JooqPin.initConfig(pool);
            CONFIGURATION.put(database.getJdbcUrl(), configuration);
        }
        return getDao(clazz, configuration);
    }

    private static <T> T getDao(final Class<T> clazz, final Configuration configuration) {
        CubeFn.outError(LOGGER,null == vertxRef,  JooqVertxNullException.class);
        return ReflectionUtils.singleton(clazz, configuration, ReactiveDatabaseClientProvider.getInstance().getClient());
    }

    public static DSLContext getDSL(final String key) {
        final Configuration configuration = CONFIGURATION.get(key);
        CubeFn.outError(LOGGER,null == configuration,  JooqConfigurationException.class);
        return Objects.isNull(configuration) ? null : configuration.dsl();
    }

    public static DSLContext getDSL() {
        return getDSL("provider");
    }

    @Override
    public Configuration get() {
        return this.get("provider");
    }

    public Configuration get(final String key) {
        return CONFIGURATION.get(key);
    }
}
