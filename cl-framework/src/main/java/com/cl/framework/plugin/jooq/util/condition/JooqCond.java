package com.cl.framework.plugin.jooq.util.condition;

import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.plugin.jooq.util.query.Criteria;
import com.cl.framework.plugin.jooq.util.query.Inquiry;
import com.cl.framework.plugin.jooq.util.query.Sorter;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.impl.DSL;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
public class JooqCond {

    private static final Logger LOGGER = LoggerFactory.getLogger(JooqCond.class);

    private static String applyField(final String field, final Function<String, String> fnTable) {
        final Set<String> keywords = new HashSet<String>() {
            {
                this.add("KEY");
            }
        };
        final StringBuilder normalized = new StringBuilder();
        if (Objects.nonNull(fnTable)) {
            normalized.append(fnTable.apply(field)).append(".");
        }
        normalized.append(keywords.contains(field) ? "`" + field + "`" : field);
        return normalized.toString();
    }

    public static List<OrderField> orderBy(final Sorter sorter, final Function<String, Field> fnAnalyze, final Function<String, String> fnTable) {
        final JsonObject sorterJson = sorter.toJson();
        final List<OrderField> orders = new ArrayList<>();
        for (final String field : sorterJson.fieldNames()) {
            final boolean asc = sorterJson.getBoolean(field);
            if (Objects.isNull(fnTable)) {
                final Field column = fnAnalyze.apply(field);
                orders.add(asc ? column.asc() : column.desc());
            } else {
                final Field original = fnAnalyze.apply(field);
                final String prefix = fnTable.apply(original.getName());
                final Field normalized = DSL.field(prefix + "." + original.getName());
                orders.add(asc ? normalized.asc() : normalized.desc());
            }
        }
        return orders;
    }

    public static Condition transform(final JsonObject filters, final Function<String, Field> fnAnalyze, final Function<String, String> fnTable) {
        return transform(filters, null, fnAnalyze, fnTable);
    }

    public static Condition transform(final JsonObject filters, final Function<String, Field> fnAnalyze) {
        return transform(filters, null, fnAnalyze);
    }

    public static Condition transform(final JsonObject filters, Operator operator, final Function<String, Field> fnAnalyze, final Function<String, String> fnTable) {
        final Condition condition;
        final Criteria criteria = Criteria.create(filters);

        if (filters != null && !filters.isEmpty()) {
            LOGGER.debug(MessageFormat.format("( JqTool ) Mode selected {0}, filters raw = {1}", criteria.getMode(), filters));
        }
        if (Inquiry.Mode.LINEAR == criteria.getMode()) {
            JsonObject inputFilters = filters;
            if (null == operator) {
                // 使用 linear模式，不处理JsonObject类型的参数条件
                inputFilters = transformLinear(filters);
                // "" 为key作为条件参数
                if (inputFilters.containsKey("")) {
                    if (inputFilters.getBoolean("")) {
                        operator = Operator.AND;
                    } else {
                        operator = Operator.OR;
                    }
                    inputFilters.remove("");
                }else{
                    // 默认是and
                    operator = Operator.AND;
                }
            } else {
                inputFilters.remove("");
            }
            condition = transformLinear(inputFilters, operator, fnAnalyze, fnTable);
        } else {
            condition = transformTree(filters, fnAnalyze, fnTable);
        }
        if (null != condition) {
            LOGGER.info(MessageFormat.format(MessageConstant.JOOQ_PARSE, condition));
        }
        return condition;
    }

    public static Condition transform(final JsonObject filters, final Operator operator, final Function<String, Field> fnAnalyze) {
        return transform(filters, operator, fnAnalyze, null);
    }

    private static Condition transformTree(final JsonObject filters, final Function<String, Field> fnAnalyze, final Function<String, String> fnTable) {
        Condition condition;
        final Operator operator = calcOperator(filters);
        final JsonObject cloned = filters.copy();
        cloned.remove("");
        final Condition linear = transformLinear(transformLinear(cloned), operator, fnAnalyze, fnTable);
        final List<Condition> tree = transformTreeSet(filters, fnAnalyze, fnTable);
        if (null != linear) {
            tree.add(linear);
        }
        if (1 == tree.size()) {
            condition = tree.get(0);
        } else {
            condition = tree.get(0);
            for (int idx = 1; idx < tree.size(); idx++) {
                final Condition right = tree.get(idx);
                condition = opCond(condition, right, operator);
            }
        }
        return condition;
    }

    private static List<Condition> transformTreeSet(final JsonObject filters, final Function<String, Field> fnAnalyze, final Function<String, String> fnTable) {
        final List<Condition> conditions = new ArrayList<>();
        final JsonObject tree = filters.copy();
        if (!tree.isEmpty()) {
            for (final String field : filters.fieldNames()) {
                if (FieldUtil.isJObject(tree.getValue(field))) {
                    conditions.add(transformTree(tree.getJsonObject(field), fnAnalyze, fnTable));
                }
            }
        }
        return conditions;
    }

    private static JsonObject transformLinear(final JsonObject filters) {
        final JsonObject linear = filters.copy();
        for (final String field : filters.fieldNames()) {
            if (FieldUtil.isJObject(linear.getValue(field))) {
                linear.remove(field);
            }
        }
        return linear;
    }

    @SuppressWarnings("all")
    private static Operator calcOperator(final JsonObject data) {
        final Operator operator;
        if (!data.containsKey("")) {
            operator = Operator.AND;
        } else {
            final Boolean isAnd = Boolean.valueOf(data.getValue("").toString());
            operator = isAnd ? Operator.AND : Operator.OR;
        }
        return operator;
    }

    private static Condition transformLinear(final JsonObject filters, final Operator operator, final Function<String, Field> fnAnalyze, final Function<String, String> fnTable) {
        Condition condition = null;
        for (final String field : filters.fieldNames()) {
            Object value = filters.getValue(field);
            if (value == null) {
                value = "";
            }

            final String[] fields;
            if (value instanceof JsonArray) {
                if (field.contains(",")) {
                    fields = field.split(",");
                } else {
                    fields = new String[2];
                    fields[0] = field;
                    fields[1] = Inquiry.Op.IN;
                }
            } else {
                fields = field.split(",");
            }

            // 确定操作符
            final String op;
            if (!field.contains(",")) {
                if (value instanceof JsonArray) {
                    op = Inquiry.Op.IN;
                } else {
                    op = Inquiry.Op.EQ;
                }
            } else {
                final String extract = fields[1];
                if (Objects.isNull(extract)) {
                    op = Inquiry.Op.EQ;
                } else {
                    op = extract.trim().toLowerCase();
                }
            }

            final String targetField = fields[0];
            final String switchedField;
            final Condition item;

            if (Objects.nonNull(fnAnalyze)) {
                final Field metaField = fnAnalyze.apply(targetField);
                CubeFn.outError(LOGGER, Objects.isNull(metaField),
                        DataTableException.class, ErrorCodeEnum.JOOQ_COND_FIELD_ERROR,
                        MessageFormat.format(ErrorInfoConstant.JOOQ_COND_FIELD_ERROR, targetField));


                final Class<?> type = metaField.getType();

                switchedField = applyField(metaField.getName().trim(), fnTable);

                final Clause clause = Clause.get(type);
                CubeFn.outError(LOGGER, Objects.isNull(clause),
                        DataTableException.class,ErrorCodeEnum.JOOQ_COND_CLAUSE_ERROR,
                        MessageFormat.format(ErrorInfoConstant.JOOQ_COND_CLAUSE_ERROR, metaField.getName(), type, targetField));

                item = clause.where(metaField, switchedField, op, value);
            } else {

                final Clause clause = Clause.get(Object.class);
                switchedField = applyField(targetField, fnTable);
                item = clause.where(null, switchedField, op, value);
            }
            condition = opCond(condition, item, operator);
        }
        return condition;
    }

    private static Condition opCond(final Condition left, final Condition right, final Operator operator) {
        if (null == left || null == right) {
            if (null == left && null != right) {
                return right;
            } else {
                return null;
            }
        } else {
            if (Operator.AND == operator) {
                return left.and(right);
            } else {
                return left.or(right);
            }
        }
    }
}
