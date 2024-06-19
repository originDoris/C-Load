package com.cl.framework.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.sqlclient.templates.annotations.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 描述：实体基类
 * @author xhz
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntity extends SuperEntity {

    private static final long serialVersionUID = 1089081615394466989L;

    /**
     * 最后修改时间
     */
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime updateTime;
    /**
     * 最后修改人ID
     */
    @Column(name = "editor")
    protected String editor;
}
