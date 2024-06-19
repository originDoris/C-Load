package com.cl.framework.core.web.core.route.aim;

import com.cl.framework.core.web.core.agent.Event;
import io.vertx.core.Handler;

/**
 * 选择处理程序
 * @author xhz
 */
public interface Aim<Context> {

    Handler<Context> attack(final Event event);
}
