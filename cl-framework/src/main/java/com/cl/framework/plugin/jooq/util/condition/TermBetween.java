package com.cl.framework.plugin.jooq.util.condition;

import com.cl.framework.core.utils.DateUtil;
import com.cl.framework.plugin.jooq.util.DateValidator;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.util.Date;

@SuppressWarnings("all")
public class TermBetween implements Term {
    @Override
    public Condition where(final Field field, final String fieldName, final Object value) {
        if (value != null) {
            String[] split = value.toString().split(",");
            Object start = null;
            Object end = null;

            if (DateValidator.isValidDate(split[0]) && DateValidator.isValidDate(split[1])) {
                start = DateValidator.convertToLocalDateTime(split[0]);
                end = DateValidator.convertToLocalDateTime(split[1]);
            }else{
                try {
                    start = Long.valueOf(split[0]);
                    end = Long.valueOf(split[1]);
                } catch (NumberFormatException e) {
                    start = split[0];
                    end = split[1];
                }
            }

            return DSL.field(fieldName).between(start, end);
        }
        return DSL.field(fieldName).between(0, 0);
    }

    protected LocalDate toDate(final Object value) {
        final Date instance = (Date) value;
        return DateUtil.toDate(instance.toInstant());
    }
}
