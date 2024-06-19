package com.cl.framework.core.annotation;



import com.cl.framework.core.web.core.param.resolver.UnsetResolver;

import java.lang.annotation.*;

/**
 * 数据流参数注解
 * @author xhz
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StreamParam {

    Class<?> resolver() default UnsetResolver.class;
}
