package com.cl.framework.plugin.jooq.util.query;

import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.FieldUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import lombok.Getter;


import java.io.Serializable;

/**
 * 条件设置
 * @author xhz
 */
public class Criteria implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Criteria.class);
    @Getter
    private final Inquiry.Mode mode;
    private transient QLinear linear;
    private transient QTree tree;

    private Criteria(final JsonObject data) {
        CubeFn.outError(LOGGER, null == data, DataTableException.class, ErrorCodeEnum.JOOQ_QUERY_META_NULL_ERROR);
        mode = parseMode(data);
        if (Inquiry.Mode.LINEAR == mode) {
            linear = QLinear.create(data);
        } else {
            tree = QTree.create(data);
        }
    }

    public static Criteria create(final JsonObject data) {
        return new Criteria(data);
    }

    /**
     * 分析条件模式，如果有jsonObject嵌套说明是tree结构的 否则是 linear 结构
     */
    private Inquiry.Mode parseMode(final JsonObject data) {
        Inquiry.Mode mode = Inquiry.Mode.LINEAR;
        for (final String field : data.fieldNames()) {
            if (FieldUtil.isJObject(data.getValue(field))) {
                mode = Inquiry.Mode.TREE;
                break;
            }
        }
        return mode;
    }

    public boolean isValid() {
        if (Inquiry.Mode.LINEAR == mode) {
            return linear.isValid();
        } else {
            return tree.isValid();
        }
    }

    public Criteria add(final String field, final Object value) {
        if (Inquiry.Mode.LINEAR == mode) {
            linear.add(field, value);
        }
        return this;
    }

    public JsonObject toJson() {
        if (Inquiry.Mode.LINEAR == mode) {
            return linear.toJson();
        } else {
            return tree.toJson();
        }
    }
}
