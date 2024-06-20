package com.cl.framework.core.domain;

import com.cl.framework.plugin.jooq.util.query.Inquiry;
import com.cl.framework.plugin.jooq.util.query.Pager;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author xhz
 * BaseQuery
 */
@Data
public class BaseQuery implements Serializable {


    protected Integer pageSize;

    protected Integer pageNo;


    public BaseQuery() {
    }
    public BaseQuery(Integer pageSize, Integer pageNo) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

    public JsonObject getPager(){
        if (pageNo != null && pageSize != null) {
            JsonObject page = new JsonObject();
            page.put(Pager.PAGE, pageNo);
            page.put(Pager.SIZE, pageSize);
            return page;
        }
        return null;
    }


    public JsonArray getSort(){
        return null;
    }

    public JsonObject getCriteria(){
        return null;
    }


    public JsonArray getProjection(){
        return null;
    }

    public Inquiry getInquiry(){
        JsonObject entries = new JsonObject();
        JsonObject pager = getPager();
        if (pager != null) {
            entries.put(Inquiry.KEY_PAGER, pager);
        }
        JsonArray sort = getSort();
        if (sort != null) {
            entries.put(Inquiry.KEY_SORTER, sort);
        }
        JsonObject criteria = getCriteria();
        if (criteria != null) {
            entries.put(Inquiry.KEY_CRITERIA, getCriteria());
        }
        JsonArray projection = getProjection();
        if (projection != null) {
            entries.put(Inquiry.KEY_PROJECTION, getProjection());
        }
        return Inquiry.create(entries);
    }

}
