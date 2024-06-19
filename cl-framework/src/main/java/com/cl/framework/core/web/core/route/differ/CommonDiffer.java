package com.cl.framework.core.web.core.route.differ;


import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.route.aim.Aim;
import com.cl.framework.core.web.core.route.aim.PingAim;
import com.cl.framework.core.web.core.route.aim.SyncAim;
import com.cl.framework.core.web.core.Pool;
import io.vertx.rxjava3.ext.web.RoutingContext;

import java.lang.reflect.Method;

/**
 * 禁用eventBus的请求
 * EventBus disabled for request
 * 1. SyncAim: Non-Event Bus: Request -> Response
 * 2. BlockAim: Non-Event Bus: Request -> (TRUE/FALSE)
 */
class CommonDiffer implements Differ<RoutingContext> {

    private static Differ<RoutingContext> INSTANCE = null;

    private CommonDiffer() {
    }

    public static Differ<RoutingContext> create() {
        if (null == INSTANCE) {
            synchronized (EventDiffer.class) {
                if (null == INSTANCE) {
                    INSTANCE = new CommonDiffer();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Aim<RoutingContext> build(final Event event) {
        final Method method = event.getAction();
        final Class<?> returnType = method.getReturnType();
        Aim<RoutingContext> aim = null;
        if (Void.class == returnType || void.class == returnType) {
            // One-Way
            aim = CubeFn.pool(Pool.AIMS, Thread.currentThread().getName() + "-mode-ping",
                    () -> ReflectionUtils.newInstance(PingAim.class));
        } else {
            // Request-Response
            aim = CubeFn.pool(Pool.AIMS, Thread.currentThread().getName() + "-mode-sync",
                    () -> ReflectionUtils.newInstance(SyncAim.class));
        }
        return aim;
    }
}
