package com.cl.enums;

import com.cl.constant.DslTypeConstant;
import lombok.Getter;

/**
 * DslType
 *
 * @author xhz
 */
@Getter
public enum DslType {
    GROOVY(DslTypeConstant.GROOVY,"groovy"),
    ;

    private final String code;


    private final String desc;


    DslType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
