package com.cl.framework.plugin.jooq.shared.reactive;

import com.cl.framework.plugin.jooq.shared.AbstractQueryExecutor;

import com.cl.framework.plugin.jooq.shared.postgres.PgConverter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;
import org.jooq.*;
import org.jooq.conf.ParamType;

import java.text.MessageFormat;
import java.util.Iterator;

/**
 * 表达式解析器
 *
 * @author xhz
 */
public class AbstractReactiveQueryExecutor extends AbstractQueryExecutor {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractReactiveQueryExecutor.class);


    protected AbstractReactiveQueryExecutor(Configuration configuration) {
        super(configuration);
    }

    protected Tuple getBindValues(Query query) {
        ArrayTuple bindValues = new ArrayTuple(query.getParams().size());
        Iterator<Param<?>> iterator = query.getParams().values().iterator();

        while(iterator.hasNext()) {
            Param<?> param = iterator.next();
            if (!param.isInline()) {
                Object value = this.convertToDatabaseType(param);
                bindValues.addValue(value);
            }
        }

        return bindValues;
    }

    protected <U> Object convertToDatabaseType(Param<U> param) {
        if (Enum.class.isAssignableFrom(param.getBinding().converter().toType())) {
            return param.getValue() == null ? null : ((EnumType)param.getValue()).getLiteral();
        } else if (byte[].class.isAssignableFrom(param.getBinding().converter().fromType())) {
            byte[] bytes = (byte[])param.getBinding().converter().to(param.getValue());
            return bytes == null ? null : Buffer.buffer(bytes);
        } else {
            return param.getBinding().converter() instanceof PgConverter ? ((PgConverter) param.getBinding().converter()).rowConverter().to(param.getValue()) : param.getBinding().converter().to(param.getValue());
        }
    }

    protected void log(Query query) {
        if (logger.isDebugEnabled()) {
            logger.debug(MessageFormat.format("Executing {0}", new Object[]{query.getSQL(ParamType.INLINED)}));
        }

    }

    protected String toPreparedQuery(Query query) {
        if (SQLDialect.POSTGRES.supports(this.configuration().dialect())) {
            String namedQuery = query.getSQL(ParamType.NAMED);
            return namedQuery.replaceAll("(?<!:):(?!:)", "\\$");
        } else {
            return query.getSQL();
        }
    }
}
