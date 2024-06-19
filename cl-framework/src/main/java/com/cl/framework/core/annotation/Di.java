package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * di 依赖注入 注入当前类和父类
 *
 * @author xhz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Di {
}
