package com.cl.framework.plugin.jooq.util.condition;

import com.cl.framework.core.utils.DateUtil;
import com.cl.framework.core.utils.FieldUtil;
import org.jooq.Condition;
import org.jooq.Field;

@SuppressWarnings("all")
public class ClauseInstant extends ClauseString {
    @Override
    public Condition where(final Field columnName, final String fieldName, final String op, final Object value) {
        final Class<?> type = value.getClass();
        final Object normalized = DateUtil.parseFull(value.toString());
        final Term term = this.termDate(op);
        return term.where(columnName, fieldName, normalized);
    }
}
