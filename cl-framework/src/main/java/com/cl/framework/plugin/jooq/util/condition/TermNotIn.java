package com.cl.framework.plugin.jooq.util.condition;

import com.cl.framework.core.utils.FieldUtil;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Collection;

@SuppressWarnings("all")
public class TermNotIn implements Term {
    @Override
    public Condition where(final Field field, final String fieldName, final Object value) {
        final Collection<?> values = FieldUtil.toCollection(value);
        return DSL.field(fieldName).notIn(values);
    }
}
