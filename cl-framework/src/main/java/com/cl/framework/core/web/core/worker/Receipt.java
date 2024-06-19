package com.cl.framework.core.web.core.worker;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 从@Consumer 类中@Address 注解的方法扫描的元数据
 *
 * @author xhz
 */
@Data
public class Receipt implements Serializable {

    /**
     * Event bus address.
     */
    private String address;
    /**
     * Proxy instance
     */
    private Object proxy;
    /**
     * Consume method ( Will be calculated )
     */
    private Method method;

}
