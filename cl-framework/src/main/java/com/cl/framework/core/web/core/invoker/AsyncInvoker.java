package com.cl.framework.core.web.core.invoker;


import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.AsyncUtils;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.rxjava3.core.eventbus.Message;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * Single<T> method(I)
 * @author xhz
 */
public class AsyncInvoker extends AbstractInvoker {

    @Override
    public void ensure(final Class<?> returnType, final Class<?> paramCls) {
        final boolean valid = (void.class != returnType && Void.class != returnType);
        InvokerUtil.verify(!valid, returnType, paramCls, this.getClass());
    }

    @Override
    @SuppressWarnings("all")
    public void invoke(final Object proxy, final Method method, final Message<Envelop> message) {
        final Envelop envelop = message.body();
        final Class<?> returnType = method.getReturnType();
        this.getLogger().info(MessageFormat.format(MessageConstant.MSG_FUTURE, this.getClass(), returnType));
        final Class<?> tCls = returnType.getComponentType();
        if (Envelop.class == tCls) {
            final Single<Envelop> result = ReflectionUtils.invoke(proxy, method.getName(), envelop);
            Disposable subscribe = result.subscribe(item -> {
                message.reply(item);
            });
        } else {
            final Object returnValue = this.invokeInternal(proxy, method, envelop);
            if (null == returnValue) {
                Single.defer(() -> {
                    return Single.just("");
                }).subscribe(AsyncUtils.toConsumer(message));
            } else {
                final Single future = (Single) returnValue;
                future.subscribe(AsyncUtils.toConsumer(message));
            }
        }
    }

}
