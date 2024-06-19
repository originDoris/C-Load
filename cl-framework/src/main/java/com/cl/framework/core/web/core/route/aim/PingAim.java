package com.cl.framework.core.web.core.route.aim;


import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.web.core.agent.Event;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * BlockAim: Non-Event Bus: One-Way
 * @author xhz
 */
public class PingAim extends BaseAim implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final Event event) {
        return CubeFn.getDefault(null, () -> (context) -> this.exec(() -> {
            // 1. Build TypedArgument
            final Object[] arguments = this.buildArgs(context, event);

            // 2. Method call
            final Object invoked = this.invoke(event, arguments);
            // 3. Resource model building
            final Envelop data;
            if (FieldUtil.isBoolean(invoked)) {
                data = Envelop.success(invoked);
            } else {
                data = Envelop.success(Boolean.TRUE);
            }
            // 4. Process modal
            Answer.reply(context, data, event);
        }, context, event), event);
    }
}
