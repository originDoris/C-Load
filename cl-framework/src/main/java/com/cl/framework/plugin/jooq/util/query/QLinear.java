package com.cl.framework.plugin.jooq.util.query;

import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.google.common.collect.Maps;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 线性查询构建
 * @author xhz
 */
public class QLinear {

    private static final Logger LOGGER = LoggerFactory.getLogger(QLinear.class);
    @Getter
    private final List<HashMap<String, HashMap<String, Object>>> conditions = new ArrayList<>();
    private final transient JsonObject raw = new JsonObject();

    private QLinear(final JsonObject data) {
        this.raw.mergeIn(data.copy());
        for (final String field : data.fieldNames()) {
            // Add
            this.add(field, data.getValue(field));
        }
    }

    public static QLinear create(final JsonObject data) {
        return new QLinear(data);
    }

    public boolean isValid() {
        return !this.conditions.isEmpty();
    }

    public QLinear add(final String field, final Object value) {
        final String filterField;
        final String op;
        if (field.contains(",")) {
            filterField = field.split(",")[0];
            op = field.split(",")[1];
        } else {
            filterField = field;
            op = Inquiry.Op.EQ;
        }
        CubeFn.outError(LOGGER, !Inquiry.Op.VALUES.contains(op),
                DataTableException.class, ErrorCodeEnum.JOOQ_OP_UN_SUPPORT_ERROR,
                MessageFormat.format(ErrorInfoConstant.JOOQ_OP_UN_SUPPORT_ERROR, op));
        final HashMap<String, Object> condition = Maps.newHashMap();
        condition.put(op, value);

        final HashMap<String, HashMap<String, Object>> item = Maps.newHashMap();
        item.put(filterField, condition);
        this.conditions.add(item);

        this.raw.put(field, value);
        return this;
    }

    public JsonObject toJson() {
        return this.raw;
    }
}
