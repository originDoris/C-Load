package com.cl.framework.core.exception;

import com.cl.framework.core.enums.ErrorCodeEnum;

/**
 * 服务器内部异常
 *
 * @author xhz
 */
public class InternalServerException extends WebException{
    public InternalServerException(ErrorCodeEnum code, String message) {
        super(code, message);
    }

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(Throwable cause) {
        super(cause);
    }

    public InternalServerException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }
}
