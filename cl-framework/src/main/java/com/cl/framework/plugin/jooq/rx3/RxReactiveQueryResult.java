package com.cl.framework.plugin.jooq.rx3;

import com.cl.framework.plugin.jooq.shared.reactive.AbstractReactiveQueryResult;
import io.vertx.rxjava3.sqlclient.Row;
import io.vertx.rxjava3.sqlclient.RowSet;
import org.jooq.Field;

/**
 * RxReactiveQueryResult
 *
 * @author xhz
 */
public class RxReactiveQueryResult extends AbstractReactiveQueryResult<Row, RowSet<Row>> {

    public RxReactiveQueryResult(RowSet<Row> result) {
        super(result);
    }

    RxReactiveQueryResult(Row row) {
        super(row);
    }

    @Override
    public <T> T get(Field<T> field) {
        return supplyOrThrow(()-> current.get(field.getType(), field.getName()));
    }

    @Override
    public <T> T get(int index, Class<T> type) {
        return supplyOrThrow(() -> current.get(type, index));
    }

    @Override
    public <T> T get(String columnName, Class<T> type) {
        return supplyOrThrow(() -> current.get(type, columnName));
    }

    @Override
    protected RxReactiveQueryResult newInstance(Row result) {
        return new RxReactiveQueryResult(result);
    }

}
