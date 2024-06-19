package com.cl.framework.plugin.jooq.util.query;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.utils.FieldUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;


import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * 复杂条件构建
 * @author xhz
 */
public class QTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(QTree.class);
    private final transient Set<QTree> trees = new HashSet<>();
    private final transient Set<String> linearKeys = new HashSet<>();
    private final transient JsonObject raw = new JsonObject();
    private transient Inquiry.Connector op;
    private transient QLinear linear;   // The same level linear;

    private QTree(final JsonObject data) {
        this.raw.mergeIn(data);
        // 计算条件 or 或 and
        this.initConnector(data);
        // 初始化结构
        this.initLinearKey(data);
        // keys
        this.initLinear(data);
    }

    public static QTree create(final JsonObject data) {
        return new QTree(data);
    }

    private void initLinear(final JsonObject data) {
        final JsonObject linear = new JsonObject();
        this.linearKeys.forEach(key -> linear.put(key, data.getValue(key)));
        this.linear = QLinear.create(linear);
    }

    private void initLinearKey(final JsonObject data) {
            final JsonObject linear = data.copy();
        linear.remove("");
        final Set<String> treeKeys = new HashSet<>();
        for (final String field : linear.fieldNames()) {
            if (!FieldUtil.isJObject(linear.getValue(field))) {
                this.linearKeys.add(field);
            } else {
                final JsonObject item = linear.getJsonObject(field);
                this.trees.add(QTree.create(item));
                treeKeys.add(field);
            }
        }
        LOGGER.debug(MessageFormat.format(MessageConstant.Q_ALL, this.linearKeys, treeKeys));
    }

    private void initConnector(final JsonObject data) {
        if (!data.containsKey("")) {
            this.op = Inquiry.Connector.AND;
        } else {
            final boolean isAnd = Boolean.parseBoolean(data.getValue("").toString());
            this.op = isAnd ? Inquiry.Connector.AND : Inquiry.Connector.OR;
        }
        LOGGER.debug(MessageFormat.format(MessageConstant.Q_STR, this.op));
    }

    public boolean isValid() {
        return null != this.linear || !this.trees.isEmpty();
    }

    public JsonObject toJson() {
        return this.raw;
    }
}
