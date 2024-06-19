package com.cl.framework.plugin.jooq.util.condition;

import org.jooq.Condition;
import org.jooq.Field;


@SuppressWarnings("all")
public interface Term {

    Condition where(final Field field, final String fieldName, final Object value);
}
