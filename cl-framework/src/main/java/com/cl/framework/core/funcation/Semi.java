package com.cl.framework.core.funcation;

import io.vertx.core.impl.logging.Logger;

import java.util.function.Supplier;

/**
 * if 语句，condition 满足条件 执行第一个函数 ，否则执行第二个函数
 *
 * @author xhz
 */
final class Semi {

    private Semi() {
    }

    static <T> T execReturn(final Supplier<T> supplier, final T defaultValue) {
        final T ret = supplier.get();
        return null == ret ? defaultValue : ret;
    }

    static void exec(final boolean condition, final SafeActuator tSupplier, final SafeActuator fSupplier, Logger logger) {
        Defend.dataCubeVoid(() -> execDataTable(condition,
                () -> {
                    if (null != tSupplier) {
                        tSupplier.execute();
                    }
                    return null;
                }, () -> {
                    if (null != fSupplier) {
                        fSupplier.execute();
                    }
                    return null;
                }), logger);
    }

    static <T> T execDataTable(final boolean condition, final Supplier<T> tSupplier, final Supplier<T> fSupplier) throws com.cl.framework.core.exception.DataTableException {
        T ret = null;
        if (condition) {
            if (null != tSupplier) {
                ret = tSupplier.get();
            }
        } else {
            if (null != fSupplier) {
                ret = fSupplier.get();
            }
        }
        return ret;
    }
}
