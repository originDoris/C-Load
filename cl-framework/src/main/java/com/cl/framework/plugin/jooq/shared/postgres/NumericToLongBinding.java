package com.cl.framework.plugin.jooq.shared.postgres;

import io.vertx.sqlclient.data.Numeric;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;

/**
 * @author xhz
 */
public class NumericToLongBinding implements Binding<BigInteger, Numeric> {


    private final Converter<BigInteger, Numeric> converter = new Converter<>() {
        @Override
        public Numeric from(BigInteger databaseObject) {
            return databaseObject == null ? null : Numeric.create(databaseObject.longValue());
        }

        @Override
        public BigInteger to(Numeric userObject) {
            return userObject == null ? null : BigInteger.valueOf(userObject.longValue());
        }

        @Override
        public Class<BigInteger> fromType() {
            return BigInteger.class;
        }

        @Override
        public Class<Numeric> toType() {
            return Numeric.class;
        }
    };

    @Override
    public Converter<BigInteger, Numeric> converter() {
        return converter;
    }

    @Override
    public void sql(BindingSQLContext<Numeric> ctx) throws SQLException {
        ctx.render().visit(DSL.val(ctx.value()));
    }

    @Override
    public void register(BindingRegisterContext<Numeric> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.NUMERIC);
    }

    @Override
    public void set(BindingSetStatementContext<Numeric> ctx) throws SQLException {
        ctx.statement().setLong(ctx.index(), ctx.value().longValue());
    }

    @Override
    public void set(BindingSetSQLOutputContext<Numeric> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetResultSetContext<Numeric> ctx) throws SQLException {
        ctx.value(converter.from(BigInteger.valueOf(ctx.resultSet().getLong(ctx.index()))));
    }

    @Override
    public void get(BindingGetStatementContext<Numeric> ctx) throws SQLException {
        ctx.value(converter.from(BigInteger.valueOf(ctx.statement().getLong(ctx.index()))));
    }

    @Override
    public void get(BindingGetSQLInputContext<Numeric> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
