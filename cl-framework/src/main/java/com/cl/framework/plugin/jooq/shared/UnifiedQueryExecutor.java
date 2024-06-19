package com.cl.framework.plugin.jooq.shared;


import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.ResultQuery;

import java.util.function.Function;

/**
 * 统一执行查询
 *
 * @author xhz
 */
public interface UnifiedQueryExecutor<EXECUTE, QUERY>  extends BasicQueryExecutor<EXECUTE> {

    <R extends Record> QUERY query(Function<DSLContext, ? extends ResultQuery<R>> queryFunction);

}
