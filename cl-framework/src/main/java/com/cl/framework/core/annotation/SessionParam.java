package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * session参数注解
 * @author xhz
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionParam {
    String value();
}
