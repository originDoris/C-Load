package com.cl.framework.core.annotation;

import com.cl.framework.core.constants.DefaultConstant;
import com.cl.framework.core.enums.MessageModel;

import java.lang.annotation.*;

/**
 * Worker 将按照@Address 使用事件总线
 *
 * @author xhz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Worker {

    MessageModel value() default MessageModel.REQUEST_RESPONSE;

    int instances() default DefaultConstant.DEFAULT_INSTANCES;

    String group() default DefaultConstant.DEFAULT_GROUP;

    boolean ha() default false;
}
