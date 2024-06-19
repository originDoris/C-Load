package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * 该注解必须在@Wall注解内部
 * 进行用户验证
 * @author xhz
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Authenticate {
    
}
