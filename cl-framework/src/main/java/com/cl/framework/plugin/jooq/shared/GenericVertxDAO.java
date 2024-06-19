package com.cl.framework.plugin.jooq.shared;


import org.jooq.Condition;
import org.jooq.DAO;
import org.jooq.OrderField;
import org.jooq.UpdatableRecord;

import java.util.Collection;

/**
 * GenericVertxDAO
 *
 * @author xhz
 */
public interface GenericVertxDAO<R extends UpdatableRecord<R>, P, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> {

    EXECUTE insert(P pojo);

    EXECUTE insert(P pojo, boolean onDuplicateKeyIgnore);

    EXECUTE insert(Collection<P> pojos);

    EXECUTE insert(Collection<P> pojos, boolean onDuplicateKeyIgnore);

    INSERT_RETURNING insertReturningPrimary(P pojo);

    EXECUTE update(P pojo);

    EXECUTE deleteById(T id);

    EXECUTE deleteByIds(Collection<T> ids);

    EXECUTE deleteByCondition(Condition condition);

    FIND_ONE findOneByCondition(Condition condition);

    FIND_ONE findOneById(T id);

    FIND_MANY findManyByIds(Collection<T> ids);

    FIND_MANY findManyByCondition(Condition condition);

    FIND_MANY findManyByCondition(Condition condition, int limit);

    FIND_MANY findManyByCondition(Condition condition, OrderField<?>... orderFields);

    FIND_MANY findManyByCondition(Condition condition, int limit, OrderField<?> ... orderFields);

    FIND_MANY findManyByCondition(Condition condition, int limit, int offset, OrderField<?> ... orderFields);

    FIND_MANY findAll();

    QueryExecutor<R,T,FIND_MANY,FIND_ONE,EXECUTE,INSERT_RETURNING> queryExecutor();
}
