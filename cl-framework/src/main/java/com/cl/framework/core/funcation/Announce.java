package com.cl.framework.core.funcation;

import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.impl.logging.Logger;
import lombok.extern.slf4j.Slf4j;


/**
 * 打印系统每一个的发生错误
 *
 * @author xhz
 */
@Slf4j
final class Announce {

    private Announce() {
    }


    static void toRun(Logger logger, final Actuator actuator) {
        try {
            actuator.execute();
        } catch (final com.cl.framework.core.exception.DataTableException ex) {
            logger.warn("execute DataTableException:", ex);
            throw ex;
        } catch (final Throwable ex) {
            logger.warn("execute Throwable:", ex);
        }
    }

    static void outUp(final Class<? extends com.cl.framework.core.exception.DataTableException> upClass,
                      Logger logger,
                      final Object... args) {
        com.cl.framework.core.exception.DataTableException error = ReflectionUtils.newInstance(upClass, args);
        logger.warn(error.getMessage(), error);
        throw error;
    }

}
