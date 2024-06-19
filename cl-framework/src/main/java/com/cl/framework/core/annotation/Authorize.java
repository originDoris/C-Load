package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * 配合@wall注解使用
 * @author xhz
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Authorize {
}
