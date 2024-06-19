package com.cl.framework.core.vertx;

import io.vertx.rxjava3.ext.web.Router;

/**
 * 描述：路由配置接口
 */
public interface RouterConfig {
    void init(final Router router);
}
