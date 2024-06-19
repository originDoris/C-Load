package com.cl.framework.core.domain;


import com.cl.framework.core.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.Getter;

/**
 * @author xhz
 * Result
 */
@Getter
@Data
public class Result<T> {
    public static final String SUCCESS_MESSAGE = "操作成功";

    private Integer code;

    private String message;

    private T data;

    private boolean success;


    public Result(T data) {
        this.code = ErrorCodeEnum.SUCCESS.getCode();
        this.message = SUCCESS_MESSAGE;
        this.data = data;
        this.success = true;
    }

    public Result(Integer code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
        this.success = false;
    }


    public Result(String message, T data) {
        this.code = ErrorCodeEnum.SUCCESS.getCode();
        this.message = message;
        this.data = data;
        this.success = true;
    }


}
