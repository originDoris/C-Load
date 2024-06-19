package com.cl.framework.core.web.core.route.differ;

import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.route.aim.Aim;
/**
 * 构建不同的工作流
 *
 * @author xhz
 */
public interface Differ<Context> {

    Aim<Context> build(Event event);
}
