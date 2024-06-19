package com.cl.framework.core.annotation;


import com.cl.framework.core.web.core.job.DefaultClass;

import java.lang.annotation.*;

/**
 * 用来定义启动job 的地方
 * @author xhz
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface On {

    String address() default "";

    Class<?> income() default DefaultClass.class;
}
