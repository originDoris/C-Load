package com.cl.framework.core.domain;

import io.vertx.sqlclient.templates.annotations.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 描述：多租户实体基类
 * @author xhz
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TenantEntity extends BaseEntity {

    private static final long serialVersionUID = -2646098561526173729L;

    /**
     * 项目ID
     */
    @Column(name = "tenant_code")
    private String tenantCode = "DataTable";
}
