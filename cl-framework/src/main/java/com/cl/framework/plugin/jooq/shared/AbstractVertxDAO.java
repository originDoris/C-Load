package com.cl.framework.plugin.jooq.shared;


import io.vertx.core.impl.Arguments;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.function.Function;

import static org.jooq.impl.DSL.row;

/**
 * AbstractVertxDAO
 *
 * @author xhz
 */
public abstract class AbstractVertxDAO<R extends UpdatableRecord<R>, P, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> implements GenericVertxDAO<R, P, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> {
    private final Class<P> type;
    private final Table<R> table;
    private final QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor;

    protected AbstractVertxDAO(Table<R> table, Class<P> type, QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor) {
        this.type = type;
        this.table = table;
        this.queryExecutor = queryExecutor;
    }

    public Class<P> getType() {
        return type;
    }

    public Table<R> getTable() {
        return table;
    }

    @Override
    public QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor(){
        return this.queryExecutor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EXECUTE update(P object){
        Objects.requireNonNull(object);
        return queryExecutor().execute(dslContext -> {
            R rec = dslContext.newRecord(getTable(), object);
            Condition where = DSL.trueCondition();
            UniqueKey<R> pk = getTable().getPrimaryKey();
            for (TableField<R, ?> tableField : pk.getFields()) {
                rec.changed(tableField, false);
                where = where.and(((TableField<R, Object>) tableField).eq(rec.get(tableField)));
            }
            Map<String, Object> valuesToUpdate = Arrays.stream(rec.fields()).collect(HashMap::new, (m, f) -> m.put(f.getName(), f.getValue(rec)), HashMap::putAll);
            return dslContext.update(getTable())
                    .set(valuesToUpdate)
                    .where(where);
        });
    }

    private Function<DSLContext,SelectConditionStep<R>> selectQuery(Condition condition) {
        return dslContext -> dslContext.selectFrom(getTable()).where(condition);
    }

    @Override
    public FIND_MANY findManyByCondition(Condition condition) {
        return queryExecutor().findMany(selectQuery(condition));
    }

    @Override
    public FIND_MANY findManyByCondition(Condition condition, int limit) {
        return queryExecutor().findMany(selectQuery(condition).andThen(sel -> sel.limit(limit)));
    }

    @Override
    public FIND_MANY findManyByCondition(Condition condition, OrderField<?>... orderField) {
        return queryExecutor().findMany(selectQuery(condition).andThen(sel->sel.orderBy(orderField)));
    }

    @Override
    public FIND_MANY findManyByCondition(Condition condition, int limit, OrderField<?>... orderField) {
        return queryExecutor().findMany(selectQuery(condition).andThen(sel->sel.orderBy(orderField).limit(limit)));
    }

    @Override
    public FIND_MANY findManyByCondition(Condition condition, int limit, int offset, OrderField<?>... orderFields) {
        return queryExecutor().findMany(selectQuery(condition).andThen(sel->sel.orderBy(orderFields).limit(offset,limit)));
    }

    @Override
    public FIND_MANY findManyByIds(Collection<T> ids){
        return findManyByCondition(equalKeys(ids));
    }

    @Override
    public FIND_MANY findAll() {
        return findManyByCondition(DSL.trueCondition());
    }

    @Override
    public FIND_ONE findOneById(T id){
        return findOneByCondition(equalKey(id));
    }

    @Override
    public FIND_ONE findOneByCondition(Condition condition){
        return queryExecutor().findOne(dslContext -> dslContext.selectFrom(getTable()).where(condition));
    }

    @Override
    public EXECUTE deleteByCondition(Condition condition){
        return queryExecutor().execute(dslContext -> dslContext.deleteFrom(getTable()).where(condition));
    }

    @Override
    public EXECUTE deleteById(T id){
        return deleteByCondition(equalKey(id));
    }

    @Override
    public EXECUTE deleteByIds(Collection<T> ids){
        return deleteByCondition(equalKeys(ids));
    }

    @Override
    public EXECUTE insert(P pojo){
        return insert(pojo,false);
    }

    @Override
    public EXECUTE insert(P pojo, boolean onDuplicateKeyIgnore) {
        Objects.requireNonNull(pojo);
        return queryExecutor().execute(dslContext -> {
            InsertSetMoreStep<R> insertStep = dslContext.insertInto(getTable()).set(newRecord(dslContext, pojo));
            return onDuplicateKeyIgnore?insertStep.onDuplicateKeyIgnore():insertStep;
        });
    }

    @Override
    public EXECUTE insert(Collection<P> pojos){
        return insert(pojos,false);
    }

    @Override
    public EXECUTE insert(Collection<P> pojos, boolean onDuplicateKeyIgnore) {
        Arguments.require(!pojos.isEmpty(), "No elements");
        return queryExecutor().execute(dslContext -> {
            InsertSetStep<R> insertSetStep = dslContext.insertInto(getTable());
            InsertValuesStepN<R> insertValuesStepN = null;
            for (P pojo : pojos) {
                insertValuesStepN = insertSetStep.values(newRecord(dslContext, pojo).intoArray());
            }
            return onDuplicateKeyIgnore?insertValuesStepN.onDuplicateKeyIgnore():insertValuesStepN;
        });
    }

    public INSERT_RETURNING insertReturningPrimary(P object){
        UniqueKey<?> key = getTable().getPrimaryKey();
        Objects.requireNonNull(key,()->"No primary key");
        return queryExecutor().insertReturning(
                dslContext -> dslContext.insertInto(getTable()).set(newRecord(dslContext, object)).returning(key.getFields()),
                keyConverter());
    }

    @SuppressWarnings("unchecked")
    protected Function<Object, T> keyConverter() {
        return record->{
            Objects.requireNonNull(record, () -> "Failed inserting record or no key");
            Record key1 = ((R)record).key();
            if(key1.size() == 1){
                return ((Record1<T>)key1).value1();
            }
            return (T) key1;
        };
    }

    @SuppressWarnings("unchecked")
    protected Condition equalKey(T id){
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk,()->"No primary key");
        TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        Condition condition;
        if (pk.length == 1) {
            condition = ((Field<Object>) pk[0]).equal(pk[0].getDataType().convert(id));
        }
        else {
            condition = row(pk).equal((Record) id);
        }
        return condition;
    }

    @SuppressWarnings("unchecked")
    protected Condition equalKeys(Collection<T> ids){
        UniqueKey<?> uk = getTable().getPrimaryKey();
        Objects.requireNonNull(uk,()->"No primary key");
        TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        Condition condition;
        if (pk.length == 1) {
            if (ids.size() == 1) {
                condition = equalKey(ids.iterator().next());
            }else {
                condition = pk[0].in(pk[0].getDataType().convert(ids));
            }
        }else {
            condition = row(pk).in(ids.toArray(new Record[ids.size()]));
        }
        return condition;
    }

    @SuppressWarnings("unchecked")
    protected T compositeKeyRecord(Object... values) {
        UniqueKey<R> key = table.getPrimaryKey();
        if (key == null) {
            return null;
        }

        TableField<R, Object>[] fields = (TableField<R, Object>[]) key.getFieldsArray();
        Record result = DSL.using(queryExecutor.configuration()).newRecord(fields);

        for (int i = 0; i < values.length; i++) {
            result.set(fields[i], fields[i].getDataType().convert(values[i]));
        }

        return (T) result;
    }


    protected Record newRecord(DSLContext dslContext, P pojo) {
        return setDefault(dslContext.newRecord(getTable(), pojo));
    }

    private Record setDefault(Record record) {
        int size = record.size();
        for (int i = 0; i < size; i++)
            if (record.get(i) == null) {
                @SuppressWarnings("unchecked")
                Field<Object> field = (Field<Object>) record.field(i);
                if (!field.getDataType().nullable() && !field.getDataType().identity()) {
                    record.set(field, DSL.defaultValue());
                }
            }
        return record;
    }

    protected abstract T getId(P object);
}
