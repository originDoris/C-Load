package com.cl.framework.plugin.jooq.util.query.tree;

import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.plugin.jooq.util.query.Criteria;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 查询树
 * @author xhz
 */
public class QTree {

    private final transient QNode current;
    private final transient ConcurrentMap<String, String> fieldInfo
            = new ConcurrentHashMap<>();

    private QTree(final Criteria criteria) {
        final JsonObject content = criteria.toJson();
        this.current = this.initTier(content, 0);
    }

    public static QTree create(final Criteria criteria) {
        return new QTree(criteria);
    }

    private QNode initTier(final String field, final Object value, final Integer level) {
        final QNode node;
        if (FieldUtil.isJObject(value)) {
            node = this.initTier(((JsonObject) value), level);
        } else {
            node = this.initValue(field, value).level(level);
        }
        return node;
    }

    public QNode getData() {
        return this.current;
    }

    private QValue initValue(final String field, final Object value) {
        if (field.contains(",")) {
            final String[] fieldOp = field.split(",");
            final String target = fieldOp[0].trim();
            final String opStr = fieldOp[1].trim();
            final QOp op = QOp.toOp(opStr);
            return QValue.create(target, op, value);
        } else {
            return QValue.create(field, QOp.EQ, value);
        }
    }

    private QNode initTier(final JsonObject content, final Integer level) {
        final QNode root = this.init(content, level);

        content.fieldNames().stream().filter(StringUtils::isNoneBlank).forEach(field -> {
            final QNode tier = this.initTier(field, content.getValue(field), level + 1);
            if (!root.isLeaf()) {
                ((QBranch) root).add(tier);
            }
        });
        return root;
    }

    private QNode init(final JsonObject content, final Integer level) {
        final QNode root;
        if (content.containsKey("")) {
            final Boolean isAnd = content.getBoolean("");
            root = QTier.create(isAnd ? QOp.AND : QOp.OR).level(level);
        } else {
            root = QTier.create(QOp.AND).level(level);
        }
        return root;
    }

    @Override
    public String toString() {
        return "\n" + this.current.toString();
    }
}
