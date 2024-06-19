package com.cl.framework.core.funcation;

import io.vertx.core.VertxException;
import io.vertx.core.impl.logging.Logger;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 将异常部分替换为特定语句。统一管理异常代码流。
 *
 * @author xhz
 */
@Slf4j
final class Defend {

    private Defend() {
    }

    static void dataCubeVoid(final Actuator actuator, Logger logger) {
        try {
            actuator.execute();
        } catch (final com.cl.framework.core.exception.DataTableException ex) {
            logger.warn("DataTableException:", ex);
        } catch (final VertxException ex) {
            logger.warn("VertxException:", ex);
        } catch (final Throwable ex) {
            logger.warn("Throwable:", ex);
        }
    }

    static <T> T dataCubeReturn(final Supplier<T> supplier, Logger logger) {
        T ret = null;
        try {
            ret = supplier.get();
        } catch (final com.cl.framework.core.exception.DataTableException ex) {
            logger.warn("DataTableException:", ex);
            throw ex;
        } catch (final VertxException ex) {
            logger.warn("VertxException:", ex);
        } catch (final Throwable ex) {
            logger.warn("Throwable:", ex);
        }
        return ret;
    }
}
