package com.cl.framework.plugin.jooq.shared;



import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * AbstractQueryResult
 *
 * @author xhz
 */
public abstract class  AbstractQueryResult implements QueryResult {

    protected <T> T supplyOrThrow(Supplier<T> supplier){
        if(hasResults()){
            return supplier.get();
        }
        throw new NoSuchElementException("QueryResult is empty");
    }

    @Override
    public List<QueryResult> asList() {
        return stream().collect(Collectors.toList());
    }
}
