package com.cl.framework.core.exception;

import com.cl.framework.core.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xhz
 * @Description: api执行异常
 */
@EqualsAndHashCode(callSuper = true)
public class ApiExecException extends DataTableException {

    public ApiExecException(ErrorCodeEnum code, String message) {
        super(code, message);
    }

    public ApiExecException(String message) {
        super(message);
    }

    public ApiExecException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiExecException(Throwable cause) {
        super(cause);
    }

    public ApiExecException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }
}
