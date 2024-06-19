package com.cl.framework.plugin.jooq.rx3;


import com.cl.framework.plugin.jooq.shared.QueryExecutor;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.rxjava3.sqlclient.RowSet;
import io.vertx.rxjava3.sqlclient.SqlClient;
import io.vertx.rxjava3.sqlclient.SqlConnection;
import io.vertx.rxjava3.sqlclient.Transaction;
import io.vertx.sqlclient.Row;
import org.jooq.*;
import org.jooq.impl.DefaultConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ReactiveRXQueryExecutor
 *
 * @author xhz
 */
public class ReactiveRXQueryExecutor<R extends UpdatableRecord<R>, P, T> extends ReactiveRXGenericQueryExecutor implements QueryExecutor<R, T, Single<List<P>>, Single<Optional<P>>, Single<Integer>, Single<T>> {

    private final Function<Row,P> pojoMapper;
    private final BiFunction<Function<DSLContext, ? extends InsertResultStep<R>>, Function<Object, T>, Single<T>> insertReturningDelegate;


    public ReactiveRXQueryExecutor(SqlClient delegate, Function<Row, P> pojoMapper) {
        this(new DefaultConfiguration().set(SQLDialect.POSTGRES),delegate,pojoMapper);
    }

    public ReactiveRXQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper) {
        this(configuration, delegate, pojoMapper, null);
    }

    ReactiveRXQueryExecutor(Configuration configuration, SqlClient delegate, Function<Row, P> pojoMapper, Transaction transaction) {
        super(configuration,delegate,transaction);
        this.pojoMapper = pojoMapper;
        this.insertReturningDelegate =
                configuration.dialect().family().equals(SQLDialect.POSTGRES)
                        ? (queryFunction,keyMapper) -> executeAny(queryFunction)
                        .map(rows -> rows.iterator().next())
                        .map(io.vertx.rxjava3.sqlclient.Row::getDelegate)
                        .map(keyMapper::apply)
                        : (queryFunction,keyMapper) -> executeAny(queryFunction)
                        .map(RowSet::getDelegate)
                        .map(keyMapper::apply);
    }

    @Override
    public Single<List<P>> findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findManyRow(queryFunction).map(rs -> rs.stream().map(pojoMapper).collect(Collectors.toList()));
    }

    @Override
    public Single<Optional<P>> findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        return findOneRow(queryFunction).map(val -> val.map(pojoMapper));
    }

    @Override
    public Single<T> insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, Function<Object, T> keyMapper) {
        return insertReturningDelegate.apply(queryFunction,keyMapper);
    }


    @Override
    protected io.reactivex.rxjava3.functions.Function<Transaction, ? extends ReactiveRXGenericQueryExecutor> newInstance(SqlConnection conn) {
        return transaction-> new ReactiveRXQueryExecutor<R,P,T>(configuration(),conn,pojoMapper,transaction);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Single<ReactiveRXQueryExecutor<R,P,T>> beginTransaction() {
        return (Single<ReactiveRXQueryExecutor<R,P,T>>) super.beginTransaction();
    }

    public Function<Row, P> pojoMapper() {
        return pojoMapper;
    }


    public Flowable<P> queryFlowable(Function<DSLContext, ? extends ResultQuery<R>> queryFunction, int fetchSize){
        return queryFlowable(queryFunction,fetchSize, r->{},r->{});
    }


    public Flowable<P> queryFlowable(Function<DSLContext, ? extends ResultQuery<R>> queryFunction, int fetchSize, Handler<AsyncResult<Void>> commitHandler,
                                     Handler<AsyncResult<Void>> closeHandler){
        return super.queryFlowableRow(queryFunction,fetchSize,commitHandler,closeHandler)
                .map(row -> pojoMapper().apply(row.getDelegate()));
    }
}
