package com.cl.framework.plugin.jooq.util.condition;

import com.cl.framework.core.utils.DateUtil;
import com.cl.framework.plugin.jooq.util.query.Inquiry;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;


class JooqAnder {

    private static final ConcurrentMap<String, BiFunction<String, Instant, Condition>> EQ_OPS =
            new ConcurrentHashMap<String, BiFunction<String, Instant, Condition>>() {
                {
                    this.put(Inquiry.Instant.DAY, (field, value) -> {
                        final LocalDate date = DateUtil.toDate(value);
                        return DSL.field(field).between(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
                    });
                    this.put(Inquiry.Instant.DATE, (field, value) -> {
                        final LocalDate date = DateUtil.toDate(value);
                        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        return DSL.field(field).eq(date.format(formatter));
                    });
                }
            };

    private static final ConcurrentMap<String, ConcurrentMap<String, BiFunction<String, Instant, Condition>>> EXECUTOR =
            new ConcurrentHashMap<String, ConcurrentMap<String, BiFunction<String, Instant, Condition>>>() {
                {
                    this.put(Inquiry.Op.EQ, EQ_OPS);
                }
            };

    static BiFunction<String, Instant, Condition> getExecutor(final String op, final String flag) {
        final ConcurrentMap<String, BiFunction<String, Instant, Condition>>
                executors = EXECUTOR.get(op);
        if (Objects.nonNull(executors) && !executors.isEmpty()) {
            return executors.get(flag);
        } else return null;
    }
}
