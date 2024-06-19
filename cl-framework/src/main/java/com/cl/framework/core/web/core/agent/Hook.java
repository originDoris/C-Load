package com.cl.framework.core.web.core.agent;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Hook
 *
 * @author xhz
 */
@Data
public class Hook implements Serializable {

    private Method action;

    private Integer order = 0;

    private Class<?> aClass;
}
