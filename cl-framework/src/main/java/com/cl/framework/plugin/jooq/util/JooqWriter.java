package com.cl.framework.plugin.jooq.util;

import io.reactivex.rxjava3.core.Single;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * jooq 更新操作工具类
 *
 * @author xhz
 */
public class JooqWriter {


    public static <R extends Record> UpdateConditionStep<R> updateByKey(DSLContext dslContext, Table<R> table, Object object) {
        R rec = dslContext.newRecord(table, object);
        Condition where = DSL.trueCondition();
        UniqueKey<R> pk = table.getPrimaryKey();
        for (TableField<R, ?> tableField : pk.getFields()) {
            rec.changed(tableField, false);
            where = where.and(((TableField<R, Object>) tableField).eq(rec.get(tableField)));
        }
        Map<String, Object> valuesToUpdate = Arrays.stream(rec.fields()).collect(HashMap::new, (m, f) -> {
            if (f.getValue(rec) != null) {
                m.put(f.getName(), f.getValue(rec));
            }
        }, HashMap::putAll);
        return dslContext.update(table)
                .set(valuesToUpdate)
                .where(where);
    }

    public static <R extends Record> InsertSetMoreStep<R> insert(DSLContext dslContext, Table<R> table, Object object) {
        return dslContext.insertInto(table).set(dslContext.newRecord(table, object));
    }








}
