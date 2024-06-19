package com.cl.framework.plugin.annotation;

import java.lang.annotation.*;

/**
 * rpc 插件注解
 * @author xhz
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Rpc {

}
