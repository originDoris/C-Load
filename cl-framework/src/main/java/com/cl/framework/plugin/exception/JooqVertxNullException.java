package com.cl.framework.plugin.exception;

/**
 * 表达式解析器
 *
 * @author xhz
 */
public class JooqVertxNullException extends com.cl.framework.core.exception.DataTableException {
    public JooqVertxNullException(String message) {
        super(message);
    }

    public JooqVertxNullException() {
        super("jooq vertx is null!");
    }
    public JooqVertxNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public JooqVertxNullException(Throwable cause) {
        super(cause);
    }

}
