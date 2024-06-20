package com.cl.service;

import com.cl.framework.plugin.jooq.rx3.VertxDAO;
import com.cl.framework.plugin.jooq.util.JqAnalyzer;
import com.cl.framework.plugin.jooq.util.condition.JooqCond;
import io.vertx.core.json.JsonObject;
import org.jooq.Condition;

/**
 * BaseService
 *
 * @author xhz
 */
public interface BaseService<T> {
    T getDao();

    default JqAnalyzer jqAnalyzer(final VertxDAO vertxDAO) {
        return JqAnalyzer.get(vertxDAO);
    }

    default Condition condition(final JsonObject entries, final VertxDAO vertxDAO) {
        return JooqCond.transform(entries, jqAnalyzer(vertxDAO)::column, null);
    }
}
