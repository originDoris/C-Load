package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * 启动类注解
 *
 * @author xhz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface StartUp {


    String[] scanBasePackages() default {};
}
