package com.cl.framework.plugin.jooq.shared;

import org.jooq.DSLContext;
import org.jooq.InsertResultStep;
import org.jooq.ResultQuery;
import org.jooq.UpdatableRecord;

import java.util.function.Function;

/**
 * 用于执行Jooq查询
 *
 * @param <R>                org.jooq.Record
 * @param <T>                主键类型
 * @param <FIND_MANY>        findMany方法的返回类型
 * @param <FIND_ONE>         findOne方法的返回类型
 * @param <EXECUTE>          插入 删除 更新 方法的返回类型
 * @param <INSERT_RETURNING> insertReturning 方法的返回类型
 * @author xhz
 */
public interface QueryExecutor<R extends UpdatableRecord<R>, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> extends BasicQueryExecutor<EXECUTE> {


    FIND_MANY findMany(Function<DSLContext, ? extends ResultQuery<R>> queryFunction);

    FIND_ONE findOne(Function<DSLContext, ? extends ResultQuery<R>> queryFunction);

    INSERT_RETURNING insertReturning(Function<DSLContext, ? extends InsertResultStep<R>> queryFunction, Function<Object,T> keyMapper);

}
