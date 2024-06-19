package com.cl.framework.core.web.core.route.aim;

import com.cl.framework.core.constants.ParamIdConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.web.core.agent.Event;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import io.vertx.rxjava3.ext.web.RoutingContext;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 请求响应规范化
 * @author xhz
 */
public final class Answer {


    public static void next(final RoutingContext context, final Envelop envelop) {
        if (envelop.valid()) {
            context.put(ParamIdConstant.REQUEST_BODY, envelop);
            context.next();
        } else {
            reply(context, envelop);
        }
    }



    public static void reply(final RoutingContext context, final Envelop envelop) {
        reply(context, envelop, new HashSet<>());
    }

    public static void reply(final RoutingContext context, final Envelop envelop, final Supplier<Set<MediaType>> supplier) {
        Set<MediaType> produces = Objects.isNull(supplier) ? new HashSet<>() : supplier.get();
        if (Objects.isNull(produces)) {
            produces = new HashSet<>();
        }
        reply(context, envelop, produces);
    }

    public static void reply(final RoutingContext context, final Envelop envelop, final Event event) {
        Set<MediaType> produces;
        if (Objects.isNull(event)) {
            produces = new HashSet<>();
        } else {
            produces = event.getProduces();
            if (Objects.isNull(produces)) {
                produces = new HashSet<>();
            }
        }
        reply(context, envelop, produces, Objects.isNull(event) ? null : event.getAction());
    }

    private static void reply(final RoutingContext context, final Envelop envelop, final Set<MediaType> mediaTypes) {
        reply(context, envelop, mediaTypes, null);
    }

    private static void reply(final RoutingContext context, final Envelop envelop, final Set<MediaType> mediaTypes, final Method sessionAction) {

        HttpServerResponse response = context.response();
        if (!response.closed()) {
            ErrorCodeEnum status = envelop.getStatus();
            response.setStatusCode(200);
            response.setStatusMessage(status.getMessage());
            envelop.bind(context);

            Outcome.media(response, mediaTypes);

            if (envelop.valid()) {
                Outcome.security(response);
            }

            Outcome.out(response, envelop, mediaTypes);
        }
    }

}
