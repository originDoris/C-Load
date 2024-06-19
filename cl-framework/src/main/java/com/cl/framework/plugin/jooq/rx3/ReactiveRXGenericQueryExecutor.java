package com.cl.framework.plugin.jooq.rx3;


import com.cl.framework.plugin.jooq.shared.QueryResult;
import com.cl.framework.plugin.jooq.shared.reactive.AbstractReactiveQueryExecutor;
import com.cl.framework.plugin.jooq.shared.reactive.ReactiveQueryExecutor;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.rxjava3.sqlclient.*;
import io.vertx.rxjava3.sqlclient.Transaction;
import io.vertx.sqlclient.Row;
import org.jooq.*;
import org.jooq.Query;
import org.jooq.exception.TooManyRowsException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * ReactiveRXGenericQueryExecutor
 *
 * @author xhz
 */
public class ReactiveRXGenericQueryExecutor extends AbstractReactiveQueryExecutor implements ReactiveQueryExecutor<Single<List<Row>>, Single<Optional<Row>>, Single<Integer>>, RXQueryExecutor {
    protected final SqlClient delegate;
    protected final Transaction transaction;


    public ReactiveRXGenericQueryExecutor(Configuration configuration, SqlClient delegate) {
        this(configuration, delegate, null);
    }

    ReactiveRXGenericQueryExecutor(Configuration configuration, SqlClient delegate, Transaction transaction) {
        super(configuration);
        this.delegate = delegate;
        this.transaction = transaction;
    }

    @Override
    public <Q extends Record> Single<List<Row>> findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return executeAny(queryFunction).map(res ->
                StreamSupport
                        .stream(rxGetDelegate(res).spliterator(), false)
                        .collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    io.vertx.sqlclient.RowSet<Row> rxGetDelegate(RowSet<io.vertx.rxjava3.sqlclient.Row> res) {
        return res.getDelegate();
    }

    @Override
    public <Q extends Record> Single<Optional<Row>> findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction) {
        return executeAny(queryFunction).map(res-> {
            switch (res.size()) {
                case 0: return Optional.empty();
                case 1: return Optional.ofNullable(rxGetDelegate(res).iterator().next());
                default: throw new TooManyRowsException(String.format("Found more than one row: %d", res.size()));
            }
        });
    }

    @Override
    public Single<Integer> execute(Function<DSLContext, ? extends Query> queryFunction) {
        return executeAny(queryFunction).map(SqlResult::rowCount);
    }

    protected Tuple rxGetBindValues(Query query) {
        Tuple tuple = Tuple.tuple();
        query.getParams()
                .values()
                .stream()
                .filter(param -> !param.isInline())
                .map(this::convertToDatabaseType)
                .forEach(tuple::addValue);
        return tuple;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Record> Single<QueryResult> query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction) {
        try{
            Query query = createQuery(queryFunction);
            log(query);
            Single<RowSet<io.vertx.rxjava3.sqlclient.Row>> rowSingle = delegate.preparedQuery(toPreparedQuery(query)).rxExecute(rxGetBindValues(query));
            return rowSingle.map(RxReactiveQueryResult::new);
        }catch (Throwable e){
            return Single.error(e);
        }
    }


    public Single<? extends ReactiveRXGenericQueryExecutor> beginTransaction(){
        if(transaction != null){
            return Single.error(new IllegalStateException("Already in transaction"));
        }
        return delegateAsPool()
                .flatMap(Pool::rxGetConnection)
                .flatMap(conn->conn.rxBegin().map(newInstance(conn)));
    }

    protected io.reactivex.rxjava3.functions.Function<Transaction, ? extends ReactiveRXGenericQueryExecutor> newInstance(SqlConnection conn) {
        return transaction -> new ReactiveRXGenericQueryExecutor(configuration(),conn,transaction);
    }


    public Completable commit(){
        if (transaction == null) {
            return Completable.error(new IllegalStateException("Not in transaction"));
        }
        return transaction.rxCommit()
                .andThen(Completable.defer(delegate::close))
                .onErrorResumeNext(x-> delegate.close().andThen(Completable.error(x)));
    }

    public Completable rollback(){
        if(transaction==null){
            return Completable.error(new IllegalStateException("Not in transaction"));
        }
        return transaction.rxRollback()
                .andThen(Completable.defer(delegate::close))
                .onErrorResumeNext(x-> delegate.close().andThen(Completable.error(x)));
    }

    public <U> Maybe<U> transaction(io.reactivex.rxjava3.functions.Function<ReactiveRXGenericQueryExecutor, Maybe<U>> transaction){
        return delegateAsPool()
                .flatMapMaybe(pool -> pool.rxWithTransaction(sqlConnection -> {
                    try {
                        return transaction.apply(new ReactiveRXGenericQueryExecutor(configuration(), sqlConnection));
                    } catch (Throwable e) {
                        return Maybe.error(e);
                    }
                }));
    }

    @Override
    public void release() {
        if(delegate!=null){
            delegate.close();
        }
    }


    public Single<RowSet<io.vertx.rxjava3.sqlclient.Row>> executeAny(Function<DSLContext, ? extends Query> queryFunction) {
        try{
            Query query = createQuery(queryFunction);
            log(query);
            return delegate.preparedQuery(toPreparedQuery(query)).rxExecute(rxGetBindValues(query));
        }catch (Throwable e){
            return Single.error(e);
        }
    }

    public Flowable<io.vertx.rxjava3.sqlclient.Row> queryFlowableRow(Function<DSLContext, ? extends Query> queryFunction, int fetchSize){
        return queryFlowableRow(queryFunction,fetchSize, r->{}, r->{});
    }

    public Flowable<io.vertx.rxjava3.sqlclient.Row> queryFlowableRow(Function<DSLContext, ? extends Query> queryFunction,
                                                                     int fetchSize,
                                                                     Handler<AsyncResult<Void>> commitHandler,
                                                                     Handler<AsyncResult<Void>> closeHandler){
        Query query = createQuery(queryFunction);
        return delegateAsPool()
                .flatMap(Pool::rxGetConnection)
                .flatMapPublisher(conn -> conn
                        .rxBegin()
                        .flatMapPublisher(tx ->
                                conn
                                        .rxPrepare(toPreparedQuery(query))
                                        .flatMapPublisher(preparedQuery -> preparedQuery.createStream(fetchSize).toFlowable())
                                        .doAfterTerminate(() -> tx.getDelegate().commit(commitHandler))
                                        .doAfterTerminate(() -> conn.getDelegate().close(closeHandler))
                        )
                );
    }

    protected Single<Pool> delegateAsPool(){
        if(!(delegate instanceof Pool)){
            return Single.error(new IllegalStateException("delegate must be an instance of Pool. Are you calling from inside a transaction?"));
        }
        Pool pool = (Pool) this.delegate;
        return Single.just(pool);
    }
}
