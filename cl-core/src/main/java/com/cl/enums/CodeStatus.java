package com.cl.enums;

import lombok.Getter;

/**
 * CodeStatus
 *
 * @author xhz
 */
@Getter
public enum CodeStatus {
    PUBLISH("publish", "发布"),
    NORMAL("normal","正常"),
    DISABLE("disable", "禁用"),;
    private final String code;

    private final String desc;


    CodeStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
