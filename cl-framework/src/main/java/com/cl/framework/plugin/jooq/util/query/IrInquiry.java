package com.cl.framework.plugin.jooq.util.query;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.utils.JsonUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.Set;

public class IrInquiry implements Inquiry {

    private static Logger LOGGER = LoggerFactory.getLogger(IrInquiry.class);

    private transient Pager pager;
    private transient Sorter sorter;
    private transient Set<String> projection;
    private transient Criteria criteria;

    IrInquiry(final JsonObject input) {
        this.ensure(input);
        // Building
        this.init(input);
    }

    @SuppressWarnings("unchecked")
    private void init(final JsonObject input) {
        if (input == null) {
            return;
        }
        CubeFn.safeSemi(input.containsKey(KEY_PAGER),
                () -> this.pager = Pager.create(input.getJsonObject(KEY_PAGER)), null, LOGGER);
        CubeFn.safeSemi(input.containsKey(KEY_SORTER),
                () -> this.sorter = Sorter.create(input.getJsonArray(KEY_SORTER)), null, LOGGER);
        CubeFn.safeSemi(input.containsKey(KEY_PROJECTION),
                () -> this.projection = new HashSet<String>(input.getJsonArray(KEY_PROJECTION).getList()), null, LOGGER);
        CubeFn.safeSemi(input.containsKey(KEY_CRITERIA),
                () -> this.criteria = Criteria.create(input.getJsonObject(KEY_CRITERIA)),null, LOGGER);
    }

    private void ensure(final JsonObject input) {
        Inquiry.ensureType(input, KEY_SORTER, JsonArray.class, FieldUtil::isJArray, this.getClass());
        Inquiry.ensureType(input, KEY_PROJECTION, JsonArray.class, FieldUtil::isJArray, this.getClass());
        Inquiry.ensureType(input, KEY_PAGER, JsonObject.class, FieldUtil::isJObject, this.getClass());
        Inquiry.ensureType(input, KEY_CRITERIA, JsonObject.class, FieldUtil::isJObject, this.getClass());
    }

    @Override
    public Set<String> getProjection() {
        return this.projection;
    }

    @Override
    public Pager getPager() {
        return this.pager;
    }

    @Override
    public Sorter getSorter() {
        return this.sorter;
    }

    @Override
    public Criteria getCriteria() {
        return this.criteria;
    }

    @Override
    public void setInquiry(final String field, final Object value) {
        if (null == this.criteria) {
            this.criteria = Criteria.create(new JsonObject());
        }
        this.criteria.add(field, value);
    }

    @Override
    public JsonObject toJson() {
        final JsonObject result = new JsonObject();
        CubeFn.safeNull(() -> result.put(KEY_PAGER, this.pager.toJson()), this.pager);
        CubeFn.safeNull(() -> {
            final JsonObject sorters = this.sorter.toJson();
            final JsonArray array = new JsonArray();
            CubeFn.<Boolean>exec(sorters, (value, key) -> array.add(key + "," + (value ? "ASC" : "DESC")));
            result.put(KEY_SORTER, array);
        }, this.sorter);
        CubeFn.safeNull(() -> result.put(KEY_PROJECTION, CubeFn.toJArray(this.projection)), this.projection);
        CubeFn.safeNull(() -> result.put(KEY_CRITERIA, this.criteria.toJson()), this.criteria);
        return result;
    }
}
