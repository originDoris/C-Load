package com.cl.framework.core.web.core.route;


import com.cl.framework.core.web.core.agent.Event;

/**
 * 管理路由信息
 * @author xhz
 */
public interface Hub<Route> {

    void mount(Route route, Event event);
}
