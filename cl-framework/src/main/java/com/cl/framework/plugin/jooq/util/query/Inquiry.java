package com.cl.framework.plugin.jooq.util.query;

import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 高级条件查询接口
 * @author xhz
 */
public interface Inquiry {
    String KEY_PAGER = "pager";
    String KEY_SORTER = "sorter";
    String KEY_CRITERIA = "criteria";
    String KEY_PROJECTION = "projection";

    String[] KEY_QUERY = new String[]{KEY_CRITERIA, KEY_PAGER, KEY_PROJECTION, KEY_SORTER};

    static Inquiry create(final JsonObject data) {
        return new IrInquiry(data);
    }



    static void ensureType(final JsonObject checkJson,
                           final String key, final Class<?> type,
                           final Predicate<Object> predicate,
                           final Class<?> target) {
        CubeFn.safeNull(() -> CubeFn.safeNull(() -> CubeFn.safeSemi(checkJson.containsKey(key),
                () -> {
                    final Object check = checkJson.getValue(key);
                    CubeFn.outError(LoggerFactory.getLogger(target), !predicate.test(check),
                            DataTableException.class, ErrorCodeEnum.JOOQ_QUERY_KEY_TYPE_ERROR,
                            MessageFormat.format(ErrorInfoConstant.JOOQ_QUERY_KEY_TYPE_ERROR, target,
                                    key, type, check.getClass()));
                }, LoggerFactory.getLogger(target)), checkJson), target);

    }


    void setInquiry(String field, Object value);

    Set<String> getProjection();

    Pager getPager();

    Sorter getSorter();

    Criteria getCriteria();

    JsonObject toJson();

    enum Connector {
        AND, OR
    }

    enum Mode {
        LINEAR,
        TREE
    }

    interface Instant {
        String DAY = "day";
        String DATE = "date";
        String TIME = "time";
        String DATETIME = "datetime";
    }

    interface Op {
        String LT = "<";
        String LE = "<=";
        String GT = ">";
        String GE = ">=";
        String EQ = "=";
        String NEQ = "<>";
        String NOT_NULL = "!n";
        String NULL = "n";
        String TRUE = "t";
        String FALSE = "f";
        String IN = "i";
        String NOT_IN = "!i";
        String START = "s";
        String END = "e";
        String CONTAIN = "c";
        String BETWEEN = "between";

        String LIKE = "like";
        String NOT_LIKE = "notLike";

        Set<String> VALUES = new HashSet<String>() {
            {
                add(LT);
                add(LE);
                add(GT);
                add(GE);
                add(EQ);
                add(NEQ);
                add(NOT_NULL);
                add(NULL);
                add(TRUE);
                add(FALSE);
                add(IN);
                add(NOT_IN);
                add(START);
                add(END);
                add(CONTAIN);
                add(BETWEEN);
                add(LIKE);
                add(NOT_LIKE);
            }
        };


        HashMap<String,String> MAPS = new HashMap<String,String>() {
            {
                put("<", LT);
                put("<=", LE);
                put(">", GT);
                put(">=", GE);
                put("=", EQ);
                put("<>", NEQ);
                put("not null", NOT_NULL);
                put("is null", NULL);
                put("true", TRUE);
                put("false", FALSE);
                put("in", IN);
                put("not in", NOT_IN);
                put("start", START);
                put("end", END);
                put("contain", CONTAIN);
                put("between",BETWEEN);
                put("like", LIKE);
                put("notLike", NOT_LIKE);
            }
        };

        HashMap<String,String> NULL_MAPS = new HashMap<String,String>() {
            {
                put(NOT_NULL, "not null");
                put(NULL, "is null");
                put(TRUE, "true");
                put(FALSE, "false");
            }
        };

    }
}
