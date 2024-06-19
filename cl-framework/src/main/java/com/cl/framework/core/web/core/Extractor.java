package com.cl.framework.core.web.core;

/**
 * Extractor
 *
 * @author xhz
 */
public interface Extractor<T> {

    T extract(Class<?> clazz);
}
