package com.cl.framework.plugin.jooq;

import com.cl.framework.core.enums.DatabaseType;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.options.DataBaseOptions;
import com.cl.framework.plugin.exception.JooqConfigurationException;
import com.cl.framework.plugin.jooq.database.DataPool;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultConnectionProvider;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class JooqPin {

    private static final Logger LOGGER = LoggerFactory.getLogger(JooqPin.class);
    private static final ConcurrentMap<DatabaseType, SQLDialect> DIALECT = new ConcurrentHashMap<DatabaseType, SQLDialect>() {
        {
            this.put(DatabaseType.MYSQL, SQLDialect.MYSQL);
            this.put(DatabaseType.MYSQL5, SQLDialect.MYSQL);
            this.put(DatabaseType.POSTGRESQL, SQLDialect.POSTGRES);
            this.put(DatabaseType.MYSQL8, SQLDialect.MYSQL);
        }
    };

    private static DataPool initPool(final JsonObject databaseJson) {
        final DataBaseOptions database = new DataBaseOptions(databaseJson);
        return DataPool.create(database);
    }

    static Configuration initConfig(final DataPool pool) {
        final DataBaseOptions database = pool.getDatabase();
        final Configuration configuration = new DefaultConfiguration();
        SQLDialect dialect = DIALECT.get(database.getCategory());
        if (Objects.isNull(dialect)) {
            dialect = SQLDialect.DEFAULT;
        }
        configuration.set(dialect);
        final Connection connection = CubeFn.safeDefault(null, () -> pool.getDataSource().getConnection());
        final ConnectionProvider provider = new DefaultConnectionProvider(connection);
        configuration.set(provider);
        return configuration;
    }

    private static Configuration initConfig(final JsonObject options) {
        final DataPool pool = initPool(options);
        return initConfig(pool);
    }

    static ConcurrentMap<String, Configuration> initConfiguration(final JsonObject config) {
        final ConcurrentMap<String, Configuration> configurationMap = new ConcurrentHashMap<>();

        CubeFn.outError(LOGGER,config == null || config.isEmpty() || !config.containsKey("provider"), JooqConfigurationException.class);
        if (config != null && !config.isEmpty()) {
            config.fieldNames().stream()
                    .filter(key -> Objects.nonNull(config.getValue(key)))
                    .filter(key -> config.getValue(key) instanceof JsonObject)
                    .forEach(key -> {
                        final JsonObject options = config.getJsonObject(key);
                        final Configuration configuration = initConfig(options);
                        configurationMap.put(key, configuration);
                        LOGGER.info(MessageFormat.format("Jooq options: \n{0}", options.encodePrettily()));
                    });
        }
        return configurationMap;
    }
}
