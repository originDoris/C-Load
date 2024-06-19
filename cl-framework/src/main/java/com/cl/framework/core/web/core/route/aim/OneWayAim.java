package com.cl.framework.core.web.core.route.aim;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.web.core.agent.Event;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;

import io.vertx.core.Handler;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.eventbus.EventBus;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * OneWayAim: Event Bus: One-Way
 * @author xhz
 */
public class OneWayAim extends BaseAim implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final Event event) {
        return CubeFn.getDefault(null, () -> (context) -> this.exec(() -> {
            final Object[] arguments = this.buildArgs(context, event);

            final Object returnValue = this.invoke(event, arguments);
            final Vertx vertx = context.vertx();
            final EventBus bus = vertx.eventBus();
            final String address = this.address(event);

            Single<Envelop> single = Flower.next(context, returnValue);

            Disposable subscribe1 = single.subscribe(dataRes -> {
                Disposable subscribe = bus.<Envelop>request(address, dataRes, delivery()).subscribe((message, throwable) -> {
                    final Envelop response;
                    if (throwable == null) {
                        response = Envelop.success(Boolean.TRUE);
                    } else {
                        response = this.failure(address, throwable);
                    }
                    Answer.reply(context, response, event);
                });
            });
        }, context, event), event);
    }
}
