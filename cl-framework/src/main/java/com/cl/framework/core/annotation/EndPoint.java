package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * 路由标记 加了该注解的类会被扫描为路由
 * @author xhz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EndPoint {
}

