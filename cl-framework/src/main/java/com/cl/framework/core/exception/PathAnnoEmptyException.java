package com.cl.framework.core.exception;

import com.cl.framework.core.enums.ErrorCodeEnum;

/**
 * path注解为空！
 *
 * @author xhz
 */
public class PathAnnoEmptyException extends DataTableException{
    public PathAnnoEmptyException(ErrorCodeEnum code, String message) {
        super(code, message);
    }

    public PathAnnoEmptyException(String message) {
        super(message);
    }

    public PathAnnoEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PathAnnoEmptyException(Throwable cause) {
        super(cause);
    }

    public PathAnnoEmptyException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }
}
