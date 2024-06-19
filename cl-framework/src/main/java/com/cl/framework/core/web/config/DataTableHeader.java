package com.cl.framework.core.web.config;

import com.cl.framework.core.utils.JsonUtil;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.MultiMap;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * DataTableHeader 信息
 *
 * @author xhz
 */

@Data
public class DataTableHeader implements Serializable {


    private transient String token;
    private transient String tenantCode;


    public void fromJson(final JsonObject json) {
        com.cl.framework.core.web.config.DataTableHeader header = JsonUtil.toObject(json, com.cl.framework.core.web.config.DataTableHeader.class);
        if (Objects.nonNull(header)) {
            this.token = header.token;
            this.tenantCode = header.tenantCode;
        }
    }

    public void fromHeader(final MultiMap headers) {
        if (Objects.nonNull(headers)) {
            String tokenHeaderKey = com.cl.framework.core.runtime.DataTableConfig.getDataTableOptions().getTokenHeaderKey();
            if (StringUtils.isBlank(tokenHeaderKey)) {
                tokenHeaderKey = com.cl.framework.core.options.DataTableOptions.TOKEN_KEY;
            }

            String tenantCode = com.cl.framework.core.runtime.DataTableConfig.getDataTableOptions().getTenantCode();
            if (StringUtils.isBlank(tokenHeaderKey)) {
                tenantCode = com.cl.framework.core.options.DataTableOptions.TENANT_CODE;
            }
            this.token = headers.get(tokenHeaderKey);
            this.tenantCode = headers.get(tenantCode);
        }
    }


    public JsonObject toJson() {
        return JsonUtil.toJsonObject(this);
    }
}
