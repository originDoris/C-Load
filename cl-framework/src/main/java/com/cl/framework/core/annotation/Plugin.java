package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * 插件注解
 * @author xhz
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Plugin {
}
