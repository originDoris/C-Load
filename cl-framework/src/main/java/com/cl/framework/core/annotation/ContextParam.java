package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * 上下文参数注解
 * @author xhz
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ContextParam {
    String value();
}
