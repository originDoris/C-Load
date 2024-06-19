package com.cl.framework.plugin.jooq.util.condition;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

@SuppressWarnings("all")
public class TermStart implements Term {
    @Override
    public Condition where(final Field field, final String fieldName, final Object value) {
        return DSL.field(fieldName).startsWith(value);
    }
}
