package com.cl.framework.core.exception;

import com.cl.framework.core.enums.ErrorCodeEnum;

/**
 * DataSourceException
 *
 * @author xhz
 */
public class DataSourceException extends DataTableException{
    public DataSourceException(ErrorCodeEnum code, String message) {
        super(code, message);
    }

    public DataSourceException(String message) {
        super(message);
    }

    public DataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceException(Throwable cause) {
        super(cause);
    }

    public DataSourceException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }
}
