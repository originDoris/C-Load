package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * rpc参数 预留
 * @author xhz
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcParam {
}
