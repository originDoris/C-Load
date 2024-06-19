package com.cl.framework.core.exception;

import com.cl.framework.core.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xhz
 * @Description: 参数异常
 */
@EqualsAndHashCode(callSuper = true)
public class ParamException extends DataTableException {

    public ParamException(ErrorCodeEnum code, String message) {
        super(code, message);
    }

    public ParamException(String message) {
        super(message);
    }

    public ParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamException(Throwable cause) {
        super(cause);
    }

    public ParamException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }
}
