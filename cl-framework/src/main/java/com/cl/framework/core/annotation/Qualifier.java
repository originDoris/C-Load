package com.cl.framework.core.annotation;

import java.lang.annotation.*;

/**
 * Instead of javax.inject.Qualifier
 * @author xhz
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Qualifier {

    String value();
}
