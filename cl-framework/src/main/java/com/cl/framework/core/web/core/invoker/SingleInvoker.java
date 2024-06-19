package com.cl.framework.core.web.core.invoker;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.AsyncUtils;
import com.cl.framework.core.utils.ResultUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.invoker.AbstractInvoker;
import com.cl.framework.core.web.core.invoker.InvokerUtil;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.eventbus.Message;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * Future<Envelop> method(Envelop)
 */
@SuppressWarnings("all")
public class SingleInvoker extends AbstractInvoker {

    @Override
    public void ensure(final Class<?> returnType, final Class<?> paramCls) {
        final boolean valid = Single.class.isAssignableFrom(returnType) && paramCls == Envelop.class;
        InvokerUtil.verify(!valid, returnType, paramCls, this.getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(final Object proxy,
                       final Method method,
                       final Message<Envelop> message) {
        // Invoke directly
        final Envelop envelop = message.body();
        // Future<T>
        final Class<?> returnType = method.getReturnType();
        // Get T
        final Class<?> tCls = returnType.getComponentType();
        this.getLogger().info(MessageFormat.format(MessageConstant.MSG_FUTURE, this.getClass(), returnType));
        if (Envelop.class == tCls) {
            final Single<Envelop> result = ReflectionUtils.invoke(proxy, method.getName(), envelop);
            result.subscribe(item -> message.reply(item));
        } else {
            final Single tResult = ReflectionUtils.invoke(proxy, method.getName(), envelop);
            tResult.subscribe(res -> {
                message.reply(ResultUtil.toEnvelop(res));
            }, error -> {
                message.reply(Envelop.failure(ResultUtil.toError(AsyncUtils.class, (Throwable) error)));
            });
        }
    }
}
