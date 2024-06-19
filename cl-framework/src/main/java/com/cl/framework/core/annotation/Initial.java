package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * 服务启动后执行
 *
 * @author xhz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Initial {
}
