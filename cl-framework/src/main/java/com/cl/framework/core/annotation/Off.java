package com.cl.framework.core.annotation;

import com.cl.framework.core.web.core.job.DefaultClass;

import java.lang.annotation.*;

/**
 * @author xhz
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Off {


    String address() default "";

    Class<?> outcome() default DefaultClass.class;
}
