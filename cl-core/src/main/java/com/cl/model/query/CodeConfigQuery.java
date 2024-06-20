package com.cl.model.query;

import com.cl.framework.core.domain.BaseQuery;
import com.cl.framework.plugin.jooq.util.query.Inquiry;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * CodeConfigQuery
 *
 * @author xhz
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeConfigQuery extends BaseQuery {
    private String dsl;

    private String status;

    private Long tenantId;

    private String codeName;

    private String serverAddress;

    private String methodName;


    @Override
    public JsonObject getCriteria() {
        JsonObject criteria = new JsonObject();
        if (StringUtils.isNotBlank(codeName)) {
            JsonObject like = new JsonObject();
            like.put("codeName," + Inquiry.Op.LIKE, codeName);
            criteria.put("like", like);
        }
        if (StringUtils.isNotBlank(status)) {
            criteria.put("status", status);
        }
        if (StringUtils.isNotBlank(serverAddress)) {
            criteria.put("serverAddress", serverAddress);
        }
        if (tenantId != null) {
            criteria.put("tenantId", tenantId);
        }
        if (StringUtils.isNotBlank(methodName)) {
            criteria.put("methodName", methodName);
        }
        if (dsl != null) {
            criteria.put("dsl", dsl);
        }
        criteria.put("isDelete", 0);
        return criteria;
    }
}
