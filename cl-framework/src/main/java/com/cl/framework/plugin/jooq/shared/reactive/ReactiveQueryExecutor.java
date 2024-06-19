package com.cl.framework.plugin.jooq.shared.reactive;


import com.cl.framework.plugin.jooq.shared.BasicQueryExecutor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.ResultQuery;

import java.util.function.Function;

/**
 * 反应式
 *
 * @author xhz
 */
public interface ReactiveQueryExecutor<FIND_MANY_ROW, FIND_ONE_ROW, EXECUTE> extends BasicQueryExecutor<EXECUTE> {


    /**
     * 执行查询并异步返回结果
     */
    <Q extends Record> FIND_MANY_ROW findManyRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction);


    /**
     * 异步执行并返回一个结果 返回多个则报错
     */
    public <Q extends Record> FIND_ONE_ROW findOneRow(Function<DSLContext, ? extends ResultQuery<Q>> queryFunction);

}
