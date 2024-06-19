package com.cl.framework.core.web.core;

import com.cl.framework.core.enums.ServerType;

import java.util.concurrent.ConcurrentMap;

/**
 * 不同Verticle部署的条件
 *
 * @author xhz
 */
public interface Factor {
    ConcurrentMap<ServerType, Class<?>> agents();
}
