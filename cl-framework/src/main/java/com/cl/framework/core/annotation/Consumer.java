package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * vertx event 事件消费者
 *
 * @author xhz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Consumer {

}
