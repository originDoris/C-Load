package com.cl.framework.plugin.jooq.util.condition.date;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.LocalDate;

@SuppressWarnings("all")
public class TermDEq extends AbstractDTerm {
    @Override
    public Condition where(final Field field, final String fieldName, final Object value) {
        return this.toDate(field, () -> {
            final LocalDate date = this.toDate(value);
            final Condition min = field.ge(date.atStartOfDay());
            final Condition max = field.lt(date.plusDays(1).atStartOfDay());
            return min.and(max);
        }, () -> DSL.field(fieldName).eq(value));
    }
}
