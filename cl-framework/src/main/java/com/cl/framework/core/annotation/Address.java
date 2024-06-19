package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * event bus 事件地址
 *
 * 如何配置该注解则使用事件总线 否则同步响应
 * @author xhz
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Address {

    String value();
}
