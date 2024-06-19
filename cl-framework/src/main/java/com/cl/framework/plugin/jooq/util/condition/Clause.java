package com.cl.framework.plugin.jooq.util.condition;

import org.jooq.Condition;
import org.jooq.Field;

import java.util.Objects;

/**
 * 条件拼接选择
 * @Author: xhz
 */
@SuppressWarnings("all")
public interface Clause {

    static Clause get(final Class<?> type) {
        Clause clause = Pool.CLAUSE_MAP.get(type);
        if (Objects.isNull(clause)) {
            clause = Pool.CLAUSE_MAP.get(Object.class);
        }
        return clause;
    }

    Condition where(final Field columnName, final String fieldName, final String op, final Object value);
}
