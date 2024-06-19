package com.cl.framework.core.exception;

import com.cl.framework.core.enums.ErrorCodeEnum;
import io.vertx.core.VertxException;
import io.vertx.core.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author xhz
 * @Description: DataTableException
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class DataTableException extends VertxException {

    protected static final String CODE = "code";
    private static final String MESSAGE = "message";


    protected ErrorCodeEnum errorCodeEnum;

    public DataTableException(ErrorCodeEnum errorCodeEnum, String message) {
        super(message);
        this.errorCodeEnum = errorCodeEnum;
    }

    public DataTableException(String message) {
        super(message);
    }

    public DataTableException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataTableException(Throwable cause) {
        super(cause);
    }
    public DataTableException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.errorCodeEnum = errorCodeEnum;
    }

    public JsonObject toJson() {
        final JsonObject data = new JsonObject();
        data.put(CODE, getErrorCodeEnum().getCode());
        data.put(MESSAGE, getMessage());
        return data;
    }
}
