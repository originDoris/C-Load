package com.cl.framework.plugin.jooq.util;

import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.JsonUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.plugin.jooq.rx3.VertxDAO;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * jooq 元数据分析
 */
@SuppressWarnings("all")
public class JqAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JqAnalyzer.class);
    private static final ConcurrentMap<Integer, VertxDAO> DAO_POOL = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Integer, JqAnalyzer> JQ_ANALYZER_CONCURRENT_MAP = new ConcurrentHashMap<>();



    private transient final VertxDAO vertxDAO;
    private transient final ConcurrentMap<String, String> mapping = new ConcurrentHashMap<>();
    private transient final ConcurrentMap<String, String> revert = new ConcurrentHashMap<>();

    private transient String tableName;

    private transient ConcurrentMap<String, Field> fieldMap = new ConcurrentHashMap<>();

    private JqAnalyzer(final VertxDAO vertxDAO) {
        this.vertxDAO = CubeFn.pool(DAO_POOL, vertxDAO.hashCode(), () -> vertxDAO);
        final Table<?> tableField = ReflectionUtils.getFieldValues(this.vertxDAO, "table");

        final Class<?> typeCls = ReflectionUtils.getFieldValues(this.vertxDAO, "type");
        final java.lang.reflect.Field[] fields = ReflectionUtils.fields(typeCls);
        final Field[] columns = tableField.fields();
        this.tableName = tableField.getName();

        for (int idx = 0; idx < columns.length; idx++) {
            final Field column = columns[idx];
            final java.lang.reflect.Field field = fields[idx];
            this.fieldMap.put(field.getName(), column);
            this.mapping.put(field.getName(), column.getName());
            this.revert.put(column.getName(), field.getName());
        }
    }

    public static JqAnalyzer get(final VertxDAO vertxDAO) {
        if (JQ_ANALYZER_CONCURRENT_MAP.containsKey(vertxDAO.hashCode())) {
            return JQ_ANALYZER_CONCURRENT_MAP.get(vertxDAO.hashCode());
        }
        JQ_ANALYZER_CONCURRENT_MAP.put(vertxDAO.hashCode(),  new JqAnalyzer(vertxDAO));
        return JQ_ANALYZER_CONCURRENT_MAP.get(vertxDAO.hashCode());
    }

    String table() {
        return this.tableName;
    }

    private String columnName(final String field) {
        String targetField;
        if (this.mapping.values().contains(field)) {
            targetField = field;
        } else {
            targetField = this.mapping.get(field);
        }

        return targetField;
    }

    ConcurrentMap<String, Field> columns() {
        return this.fieldMap;
    }

    public Field column(final String field) {
        String columnField = columnName(field);
        CubeFn.outError(LOGGER, null == columnField,
                com.cl.framework.core.exception.DataTableException.class, ErrorCodeEnum.JOOQ_FIELD_MISSING_ERROR,
                MessageFormat.format(ErrorInfoConstant.JOOQ_FIELD_MISSING_ERROR, JqAnalyzer.class, field, ReflectionUtils.getFieldValues(this.vertxDAO, "type")));
        LOGGER.debug(MessageFormat.format(MessageConstant.JOOQ_FIELD, field, columnField));
        Field found;
        if (field.equals(columnField)) {
            found = this.fieldMap.get(field);
        } else {
            final String actualField = this.revert.get(columnField);
            found = this.fieldMap.get(actualField);
        }
        if (Objects.isNull(found)) {
            found = DSL.field(DSL.name(columnField));
        }
        return found;
    }


    <T> T copyEntity(final T target, final T updated, Table table) {
        CubeFn.outError(LOGGER, null == updated, com.cl.framework.core.exception.DataTableException.class, ErrorCodeEnum.JOOQ_MERGE_ERROR);
        return CubeFn.getSemi(null == target && null == updated, LOGGER, () -> null, () -> {
            final JsonObject targetJson = null == target ? new JsonObject() : JsonUtil.serializeJson(target);
            final UniqueKey key = table.getPrimaryKey();
            key.getFields().stream().map(item -> ((TableField) item).getName())
                    .filter(this.revert::containsKey)
                    .map(this.revert::get)
                    .forEach(item -> ReflectionUtils.getFieldValues(updated, item.toString(), null));
            final JsonObject sourceJson = JsonUtil.serializeJson(updated);
            targetJson.mergeIn(sourceJson, true);
            final Class<?> type = null == target ? updated.getClass() : target.getClass();
            return (T) JsonUtil.deserialize(targetJson.encode(), type);
        });
    }
}
