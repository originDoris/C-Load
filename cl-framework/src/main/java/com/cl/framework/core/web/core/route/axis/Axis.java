package com.cl.framework.core.web.core.route.axis;

/**
 * 配置路由
 *
 * @author xhz
 */
public interface Axis<Router> {
    void mount(Router router);
}
