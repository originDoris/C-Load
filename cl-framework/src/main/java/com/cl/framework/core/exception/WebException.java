package com.cl.framework.core.exception;

import com.cl.framework.core.enums.ErrorCodeEnum;

/**
 * WebException
 *
 * @author xhz
 */
public class WebException  extends DataTableException{
    public WebException(ErrorCodeEnum code, String message) {
        super(code, message);
    }

    public WebException(String message) {
        super(message);
    }

    public WebException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebException(Throwable cause) {
        super(cause);
    }

    public WebException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }
}
