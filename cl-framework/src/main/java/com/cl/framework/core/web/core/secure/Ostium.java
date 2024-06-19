package com.cl.framework.core.web.core.secure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 自定义鉴权模型
 * @author xhz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ostium implements Serializable {
    /**
     * Header parsing
     */
    private Method header;
    /**
     * 401: Authenticate method
     */
    private Method authenticate;
    /**
     * 403: Authorize method
     */
    private Method authorize;

}
