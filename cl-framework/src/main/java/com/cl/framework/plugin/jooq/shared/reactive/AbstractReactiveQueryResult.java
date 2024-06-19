package com.cl.framework.plugin.jooq.shared.reactive;


import com.cl.framework.plugin.jooq.shared.AbstractQueryResult;
import com.cl.framework.plugin.jooq.shared.QueryResult;


import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * AbstractReactiveQueryResult
 *
 * @author xhz
 */
public abstract class AbstractReactiveQueryResult<R, RS extends Iterable<R>> extends AbstractQueryResult {
    protected final R current;
    protected final RS result;

    public AbstractReactiveQueryResult(RS result) {
        this.result = result;
        this.current = result.iterator().hasNext() ? result.iterator().next() : null;
    }

    protected AbstractReactiveQueryResult(R row) {
        this.result = null;
        this.current = row;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap() {
        return (T) current;
    }

    @Override
    public boolean hasResults() {
        return current != null;
    }

    abstract protected AbstractReactiveQueryResult<R,RS> newInstance(R result);

    @Override
    public Stream<QueryResult> stream() {
        Objects.requireNonNull(result, ()->"asList() can only be called once");
        return StreamSupport.stream(result.spliterator(), false).map(this::newInstance);
    }
}
