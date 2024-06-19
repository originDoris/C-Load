package com.cl.framework.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.vertx.sqlclient.templates.annotations.Column;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 描述：实体超类
 * @author xhz
 */
@Data
public class SuperEntity implements Serializable {

    private static final long serialVersionUID = -117731573519606807L;

    /**
     * 主键
     */
    protected Long id;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createTime;
    /**
     * 创建人ID
     */
    protected String creator;

}
