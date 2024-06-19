package com.cl.framework.core.exception;

import com.cl.framework.core.enums.ErrorCodeEnum;

/**
 * MethodNullException
 *
 * @author xhz
 */
public class MethodNullException extends DataTableException{
    public MethodNullException(ErrorCodeEnum code, String message) {
        super(code, message);
    }

    public MethodNullException(String message) {
        super(message);
    }

    public MethodNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodNullException(Throwable cause) {
        super(cause);
    }

    public MethodNullException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }
}
