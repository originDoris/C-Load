package com.cl.framework.plugin.jooq.util.condition;

import com.cl.framework.core.constants.MessageConstant;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import org.jooq.Condition;
import org.jooq.Field;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@SuppressWarnings("all")
public class ClauseString implements Clause {
    @Override
    public Condition where(final Field columnName, final String fieldName, final String op, final Object value) {
        final Term term = termInternal(Pool.TERM_OBJECT_MAP, op);
        return term.where(columnName, fieldName, value);
    }

    protected Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }

    protected Term termDate(final String op) {
        return termInternal(Pool.TERM_DATE_MAP, op);
    }

    protected Object normalized(final Object value, final Function<Object, Object> convert) {
        if (value instanceof JsonArray) {
            final JsonArray result = new JsonArray();
            ((JsonArray) value).stream().map(convert)
                    .filter(Objects::nonNull).forEach(result::add);
            return result;
        } else {
            return convert.apply(value);
        }
    }

    private Term termInternal(final ConcurrentMap<String, Term> map, final String op) {
        final Term term = map.get(op);
        if (Objects.isNull(term)) {
            logger().warn(MessageFormat.format(MessageConstant.JOOQ_TERM_ERR, op));
        } else {
            logger().debug(MessageFormat.format(MessageConstant.JOOQ_TERM, term, op));
        }
        return term;
    }
}
