package com.cl.framework.plugin.jooq.database;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.options.DataBaseOptions;
import com.cl.framework.core.options.visitor.DataBaseVisitor;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.jooq.DSLContext;

import javax.sql.DataSource;
import java.text.MessageFormat;

/**
 * 数据库连接池
 * @author xhz
 */
public interface DataPool {
    static DataPool create() {
        DataBaseVisitor dataBaseVisitor = ReflectionUtils.singleton(DataBaseVisitor.class);
        return create(dataBaseVisitor.visit("jooq"));
    }

    static DataPool create(final DataBaseOptions database) {
        return CubeFn.pool(Pool.POOL_DYNAMIC, database.getJdbcUrl(), () -> {
            final Logger logger = LoggerFactory.getLogger(DataPool.class);
            final DataPool ds = new HikariDataPool(database);
            logger.info(MessageFormat.format("[ DP ] Data Pool Hash : {0}", ds.hashCode()));
            return ds;
        });
    }

    static DataPool createAuto(final DataBaseOptions database) {
        final DataPool ds = create(database);
        return ds.switchTo();
    }

    DataPool switchTo();


    DSLContext getExecutor();


    DataSource getDataSource();


    DataBaseOptions getDatabase();
}
