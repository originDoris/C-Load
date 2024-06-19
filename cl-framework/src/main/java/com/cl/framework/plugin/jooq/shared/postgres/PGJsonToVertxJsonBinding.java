package com.cl.framework.plugin.jooq.shared.postgres;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author xhz
 */
public abstract class PGJsonToVertxJsonBinding<PG_JSON,VERTX_JSON> implements Binding<PG_JSON, VERTX_JSON> {

    abstract Function<String,PG_JSON> valueOf();

    abstract String coerce();

    @Override
    public void sql(BindingSQLContext<VERTX_JSON> ctx) {
        RenderContext context = ctx.render().visit(DSL.val(ctx.convert(converter()).value()));
        context.sql(coerce());
    }

    @Override
    public void register(BindingRegisterContext<VERTX_JSON> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    public void set(BindingSetStatementContext<VERTX_JSON> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), Objects.toString(ctx.convert(converter()).value(), null));
    }

    @Override
    public void get(BindingGetResultSetContext<VERTX_JSON> ctx) throws SQLException {
        ctx.convert(converter()).value(valueOf().apply(ctx.resultSet().getString(ctx.index())));
    }

    @Override
    public void get(BindingGetStatementContext<VERTX_JSON> ctx) throws SQLException {
        ctx.convert(converter()).value(valueOf().apply(ctx.statement().getString(ctx.index())));
    }

    @Override
    public void set(BindingSetSQLOutputContext<VERTX_JSON> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetSQLInputContext<VERTX_JSON> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
