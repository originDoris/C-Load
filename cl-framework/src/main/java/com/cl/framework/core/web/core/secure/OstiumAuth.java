package com.cl.framework.core.web.core.secure;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 自定义鉴权
 * @author xhz
 */
public class OstiumAuth {
    private final transient Class<?> clazz;
    private final transient Logger logger;
    private final transient Method[] methods;

    public static OstiumAuth create(final Class<?> clazz) {
        return new OstiumAuth(clazz);
    }

    private OstiumAuth(final Class<?> clazz) {
        this.clazz = clazz;
        this.logger = LoggerFactory.getLogger(clazz);
        this.methods = clazz.getDeclaredMethods();
    }

    public OstiumAuth verify() {

        return this;
    }

    public void mount(final Cliff reference) {
        
    }
}
