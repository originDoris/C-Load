package com.cl.framework.core.web.core.route.aim;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.AsyncUtils;
import com.cl.framework.core.utils.JsonUtil;
import com.cl.framework.core.web.core.Virtual;
import com.cl.framework.core.web.core.agent.Event;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.eventbus.EventBus;
import io.vertx.rxjava3.ext.web.RoutingContext;

/**
 * 异步处理
 * @author xhz
 */
public class AsyncAim extends BaseAim implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final Event event) {
        return CubeFn.getDefault(null, () -> (context) -> this.exec(() -> {

            final Single<Envelop> future = this.invoke(context, event);
            final Vertx vertx = context.vertx();
            final EventBus bus = vertx.eventBus();
            final String address = this.address(event);
            Disposable subscribe = future.subscribe(dataRes -> {
                bus.<Envelop>request(address, dataRes, delivery())
                        .subscribe((envelopMessage, throwable) -> {
                            final Envelop response;
                            if (throwable == null) {
                                response = this.success(address, envelopMessage);
                            } else {
                                response = this.failure(address, throwable);
                            }
                            Answer.reply(context, response, event);
                        });
            }, throwable -> {
                final Envelop envelop = Envelop.failure(throwable);
                Answer.reply(context, envelop, event);
            });
        }, context, event), event);
    }

    private Single<Envelop> invoke(final RoutingContext context,
                                   final Event event) {
        final Object proxy = event.getProxy();

        final Object[] arguments = this.buildArgs(context, event);

        final Single<Envelop> invoked;
        if (Virtual.is(proxy)) {
            final JsonObject message = new JsonObject();
            for (int idx = 0; idx < arguments.length; idx++) {
                message.put(String.valueOf(idx), arguments[idx]);
            }
            invoked = Flower.next(context, message);
        } else {
            final Object returnValue = this.invoke(event, arguments);
            invoked = Flower.next(context, returnValue);
        }

        return invoked.flatMap(response -> {
            response.getAssist().setHeaders(context.request().headers());
            response.getAssist().setUser(context.user());
            response.getAssist().setSession(context.session());
            response.getAssist().setContext(context.data());
            return AsyncUtils.single(response);
        });
    }
}
