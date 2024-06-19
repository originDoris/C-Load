package com.cl.framework.plugin.jooq.util.condition;

import com.cl.framework.core.utils.FieldUtil;
import org.jooq.Condition;
import org.jooq.Field;

@SuppressWarnings("all")
public class ClauseBoolean extends ClauseString {
    @Override
    public Condition where(final Field columnName, final String fieldName, final String op, final Object value) {
        final Class<?> type = value.getClass();
        Object normalized = value;
        if (FieldUtil.isBoolean(value)) {
            normalized = normalized(value, from -> Boolean.valueOf(from.toString()));
        }
        return super.where(columnName, fieldName, op, normalized);
    }
}
