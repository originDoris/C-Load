package com.cl.framework.core.web.core.route.differ;

import com.cl.framework.core.annotation.Address;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.route.aim.Aim;
import io.vertx.rxjava3.ext.web.RoutingContext;


import java.lang.reflect.Method;

/**
 * 在路由构建时，构建执行器引用
 * 1. 区分是否启用EventBus
 * 2. 区分 one-way 和 request-response
 * 支持的模式:
 * 1. AsyncAim: Event Bus: Request-Response
 * 2. SyncAim: Non-Event Bus: Request-Response
 * 3. OneWayAim: Event Bus: One-Way
 * 4. BlockAim: Non-Event Bus: One-Way
 * 5. Vert.x Style Request -> Event -> Response
 * @author xhz
 */
public class ModeSplitter {

    public Aim<RoutingContext> distribute(final Event event) {
        return CubeFn.getDefault(null, () -> {
            final Method method = event.getAction();
            final boolean annotated = method.isAnnotationPresent(Address.class);

            final Differ<RoutingContext> differ;
            if (annotated) {
                // 1,3,5
                differ = EventDiffer.create();
            } else {
                // 2,4
                differ = CommonDiffer.create();
            }
            return differ.build(event);
        }, event, event.getAction());
    }
}
