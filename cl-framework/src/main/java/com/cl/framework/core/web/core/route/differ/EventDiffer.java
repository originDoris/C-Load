package com.cl.framework.core.web.core.route.differ;

import com.cl.framework.core.annotation.Address;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.route.axis.EventAxis;
import com.cl.framework.core.web.core.route.aim.Aim;
import com.cl.framework.core.web.core.route.aim.AsyncAim;
import com.cl.framework.core.web.core.route.aim.OneWayAim;
import com.cl.framework.core.web.core.Pool;
import com.cl.framework.core.web.core.worker.Receipt;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.eventbus.Message;
import io.vertx.rxjava3.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Set;

/**
 * 使用EventBus
 * 1. AsyncAim: Request -> Agent -> EventBus -> Worker -> Envelop Response
 * 2. OneWayAim: Request -> Agent -> EventBus -> Worker -> (TRUE/FALSE)
 * 5. Vertx AsyncAim: Request -> Agent -> EventBus -> Worker -> void Response(Replier)
 */
class EventDiffer implements Differ<RoutingContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventAxis.class);

    private static final Set<Receipt> RECEIPTS = DataTableAnno.getReceipts();

    private static Differ<RoutingContext> INSTANCE = null;

    private EventDiffer() {
    }

    public static Differ<RoutingContext> create() {
        if (null == INSTANCE) {
            synchronized (EventDiffer.class) {
                if (null == INSTANCE) {
                    INSTANCE = new EventDiffer();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Aim<RoutingContext> build(final Event event) {
        Aim<RoutingContext> aim = null;
        final Method replier = this.findReplier(event);
        final Method method = event.getAction();
        final Class<?> returnType = method.getReturnType();
        if (Void.class == returnType || void.class == returnType) {
            CubeFn.outError(LOGGER, true, DataTableException.class,
                    ErrorCodeEnum.METHOD_MUST_HAS_RETURN,
                    MessageFormat.format(ErrorInfoConstant.METHOD_MUST_HAS_RETURN, this.getClass(), method));
        } else {
            final Class<?> replierType = replier.getReturnType();
            if (Void.class == replierType || void.class == replierType) {
                if (this.isAsync(replier)) {
                    // Mode 5: Event Bus: ( Async ) Request-Response
                    aim = CubeFn.pool(Pool.AIMS, Thread.currentThread().getName() + "-mode-vert.x",
                            AsyncAim::new);
                } else {
                    // Mode 3: Event Bus: One-Way
                    aim = CubeFn.pool(Pool.AIMS, Thread.currentThread().getName() + "-mode-oneway",
                            OneWayAim::new);
                }
            } else {
                // Mode 1: Event Bus: Request-Response
                aim = CubeFn.pool(Pool.AIMS, Thread.currentThread().getName() + "-mode-java",
                        AsyncAim::new);
            }
        }
        return aim;
    }

    private boolean isAsync(final Method method) {
        boolean async = false;
        final Class<?>[] paramTypes = method.getParameterTypes();
        if (1 == paramTypes.length) {
            final Class<?> argumentCls = paramTypes[0];
            if (Message.class == argumentCls) {
                async = true;
            }
        }
        return async;
    }

    @SuppressWarnings("all")
    private Method findReplier(final Event event) {
        final Annotation annotation = event.getAction().getDeclaredAnnotation(Address.class);
        final String address = ReflectionUtils.invoke(annotation, "value");
        final Receipt found = RECEIPTS.stream()
                .filter(item -> address.equals(item.getAddress()))
                .findFirst().orElse(null);

        final Method method;
        CubeFn.outError(LOGGER, null == found, DataTableException.class,
                MessageFormat.format(ErrorInfoConstant.WORKER_MISSING_ERROR, address, this.getClass()));
        method = found.getMethod();

        CubeFn.outError(LOGGER, null == found, DataTableException.class,
                MessageFormat.format(ErrorInfoConstant.WORKER_MISSING_ERROR, address, this.getClass()));
        return method;
    }
}
