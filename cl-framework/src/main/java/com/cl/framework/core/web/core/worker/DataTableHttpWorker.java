package com.cl.framework.core.web.core.worker;

import com.cl.framework.core.annotation.Worker;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.vertx.VertxLauncher;
import com.cl.framework.core.web.core.invoker.Invoker;
import com.cl.framework.core.web.core.invoker.InvokerUtil;
import com.cl.framework.core.web.core.invoker.JetSelector;
import com.cl.framework.core.web.core.scatter.AgentScatter;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.eventbus.EventBus;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 默认HttpWorker
 * @author xhz
 */
@Worker
public class DataTableHttpWorker extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentScatter.class);

    private static final Set<Receipt> RECEIPTS = DataTableAnno.getReceipts();

    private static final ConcurrentMap<Integer, Invoker> INVOKER_MAP = new ConcurrentHashMap<>();

    private static final AtomicBoolean LOGGED = new AtomicBoolean(Boolean.FALSE);

    @Override
    public void start() {
        final EventBus bus = VertxLauncher.getVertx().eventBus();
        for (final Receipt receipt : RECEIPTS) {
            final String address = receipt.getAddress();

            final Object reference = receipt.getProxy();
            final Method method = receipt.getMethod();

            InvokerUtil.verifyArgs(method, this.getClass());
            final Class<?>[] params = method.getParameterTypes();
            final Class<?> returnType = method.getReturnType();
            final Class<?> paramCls = params[0];

            final Invoker invoker = JetSelector.select(returnType, paramCls);
            invoker.ensure(returnType, paramCls);
            INVOKER_MAP.put(receipt.hashCode(), invoker);

            CubeFn.safeJvm(() -> CubeFn.safeNull(() -> bus.<Envelop>consumer(address,
                    message -> {
                        invoker.invoke(reference, method, message);
                    }),
                    address, reference, method), LOGGER
            );
        }

        if (!LOGGED.getAndSet(Boolean.TRUE)) {
            final ConcurrentMap<Class<?>, Set<Integer>> outputMap = new ConcurrentHashMap<>();
            INVOKER_MAP.forEach((key, value) -> {
                if (outputMap.containsKey(value.getClass())) {
                    outputMap.get(value.getClass()).add(key);
                } else {
                    outputMap.put(value.getClass(), new HashSet<>());
                }
            });
            outputMap.forEach((key, value) -> LOGGER.info(MessageFormat.format(MessageConstant.MSG_INVOKER, key, FieldUtil.toString(value), String.valueOf(value.size()))));
        }
    }
}
