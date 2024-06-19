package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * 配合@WebFilter使用，用来指定filter顺序
 *
 * @author xhz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FilterOrder {

    int value() default 0;
}
