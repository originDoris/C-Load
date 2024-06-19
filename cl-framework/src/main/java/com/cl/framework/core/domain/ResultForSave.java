package com.cl.framework.core.domain;

import com.cl.framework.core.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class ResultForSave<T> {
    public static final String SUCCESS_MESSAGE = "保存成功";

    private Integer code;

    private String message;

    private T data;

    private boolean success;

    public ResultForSave(T data) {
        this.code = ErrorCodeEnum.SUCCESS.getCode();
        this.message = SUCCESS_MESSAGE;
        this.data = data;
        this.success = true;
    }
}
