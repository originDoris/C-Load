package com.cl.framework.core.funcation;

/**
 * SafeSupplier
 *
 * @author xhz
 */
@FunctionalInterface
public interface SafeSupplier<T> {

    T get() throws Exception;
}
