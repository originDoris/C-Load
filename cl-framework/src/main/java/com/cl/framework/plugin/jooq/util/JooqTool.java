package com.cl.framework.plugin.jooq.util;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.plugin.jooq.util.query.Inquiry;
import io.vertx.core.json.JsonObject;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

/**
 * jooq工具
 *
 * @author xhz
 */
public class JooqTool {

   public static Inquiry getInquiry(final JsonObject envelop) {
       return CubeFn.getDefault(Inquiry.create(new JsonObject()), () -> {
           final JsonObject data = envelop.copy();
           return Inquiry.create(data);
       }, envelop);
    }


    public static <R extends Record,P> Record newRecord(DSLContext dslContext, P pojo, Table<R> table) {
        return setDefault(dslContext.newRecord(table, pojo));
    }

    private static Record setDefault(Record record) {
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
}
