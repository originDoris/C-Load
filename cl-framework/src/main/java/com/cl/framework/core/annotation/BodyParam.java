package com.cl.framework.core.annotation;


import com.cl.framework.core.web.core.param.resolver.JsonResolver;
import com.cl.framework.core.web.core.param.resolver.Resolver;

import java.lang.annotation.*;

/**
 * body 参数注解
 *
 * @author xhz
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParam {
    Class<? extends Resolver> resolver() default JsonResolver.class;
}
