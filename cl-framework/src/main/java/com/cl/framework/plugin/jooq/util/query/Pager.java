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
 * 分页工具
 * @author xhz
 */
@Getter
public class Pager implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pager.class);
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    /**
     * Start page: >= 1
     */
    private transient int page;
    /**
     * Page size
     */
    private transient int size;
    /**
     * From index: offset
     */
    private transient int start;
    /**
     * To index: limit
     */
    private transient int end;

    private Pager(final Integer page, final Integer size) {
        this.init(page, size);
    }

    private Pager(final JsonObject pageJson) {
        this.ensure(pageJson);
        this.init(pageJson.getInteger(PAGE), pageJson.getInteger(SIZE));
    }

    public static Pager create(final Integer page, final Integer size) {
        return new Pager(page, size);
    }


    public static Pager create(final JsonObject pageJson) {
        return new Pager(pageJson);
    }

    void ensure(final JsonObject pageJson) {
        CubeFn.outError(LOGGER, null == pageJson,
                DataTableException.class,
                ErrorCodeEnum.JOOQ_QUERY_META_NULL_ERROR);

        CubeFn.outError(LOGGER, !pageJson.containsKey(PAGE), DataTableException.class, ErrorCodeEnum.JOOQ_PAGER_INVALID_ERROR);
        CubeFn.outError(LOGGER, !pageJson.containsKey(SIZE),DataTableException.class, ErrorCodeEnum.JOOQ_SIZE_INVALID_ERROR);

        Inquiry.ensureType(pageJson, PAGE, Integer.class, FieldUtil::isInteger, this.getClass());
        Inquiry.ensureType(pageJson, SIZE, Integer.class, FieldUtil::isInteger, this.getClass());
    }

    private void init(final Integer page, final Integer size) {
        CubeFn.outError(LOGGER, 1 > page, DataTableException.class, ErrorCodeEnum.JOOQ_PAGER_INVALID_ERROR);
        this.page = page;
        this.size = 0 < size ? size : 10;
        CubeFn.safeNull(() -> {
            this.start = (this.page - 1) * this.size;
            this.end = this.page * this.size;
        }, this.page, this.size);
    }

    public JsonObject toJson() {
        final JsonObject data = new JsonObject();
        data.put(PAGE, this.page);
        data.put(SIZE, this.size);
        return data;
    }

    @Override
    public String toString() {
        return "Pager{" +
                "page=" + page +
                ", size=" + size +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
