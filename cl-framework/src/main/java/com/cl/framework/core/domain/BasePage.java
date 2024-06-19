package com.cl.framework.core.domain;

import com.cl.framework.plugin.jooq.util.query.Pager;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * @author xhz
 * @Description: 基础分页
 */
@Data
public class BasePage<T> implements Serializable {
    private Integer pageNo;

    private Integer pageSize;

    private List<T> list;

    private Integer count;


    public BasePage() {
    }

    public BasePage(Integer pageNo, Integer pageSize, List<T> list, Integer count) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.list = list;
        this.count = count;
    }
}
