package com.cl.framework.core.web.core.invoker;

import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.AsyncUtils;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.utils.JsonUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.Vertx;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 支持动态调用
 * @author xhz
 */
@SuppressWarnings("all")
public abstract class AbstractInvoker implements Invoker {

    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected Future invokeJson(final Object proxy, final Method method, final Envelop envelop) {
        final Object reference = envelop.getData();
        final Class<?> argType = method.getParameterTypes()[0];
        final Object arguments = JsonUtil.deserialize(FieldUtil.toString(reference), argType);
        return ReflectionUtils.invoke(proxy, method.getName(), arguments);
    }


    protected Object invokeInternal(final Object proxy, final Method method, final Envelop envelop) {
        Object returnValue;
        final Class<?>[] argTypes = method.getParameterTypes();
        final Class<?> returnType = method.getReturnType();
        if (1 == method.getParameterCount()) {
            final Class<?> firstArg = argTypes[0];
            if (Envelop.class == firstArg) {
                returnValue = ReflectionUtils.invoke(proxy, method.getName(), envelop);
            } else {
                returnValue = InvokerUtil.invokeSingle(proxy, method, envelop);
            }
        } else {
            returnValue = InvokerUtil.invokeMulti(proxy, method, envelop);
        }
        return returnValue;
    }

    /**
     *
     */
    protected <I> Function<I, Single<Envelop>> nextEnvelop(final Vertx vertx, final Method method) {
        return item -> this.nextEnvelop(vertx, method, item);
    }

    protected <T> Single<Envelop> nextEnvelop(final Vertx vertx, final Method method, final T result) {
        return AsyncUtils.single((Envelop) result);
    }
}
