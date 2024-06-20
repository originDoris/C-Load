package com.cl.enums;

import lombok.Getter;

/**
 * DslType
 *
 * @author xhz
 */
@Getter
public enum DslType {
    GROOVY("groovy","groovy"),
    ;

    private final String code;


    private final String desc;


    DslType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
