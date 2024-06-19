package com.cl.framework.core.web.core.invoker;


import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.AsyncUtils;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.Future;
import io.vertx.rxjava3.core.eventbus.Message;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * Future<Envelop> method(Envelop)
 */
@SuppressWarnings("all")
public class FutureInvoker extends AbstractInvoker {

    @Override
    public void ensure(final Class<?> returnType,
                       final Class<?> paramCls) {
        final boolean valid = Future.class.isAssignableFrom(returnType) && paramCls == Envelop.class;
        InvokerUtil.verify(!valid, returnType, paramCls, this.getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(final Object proxy, final Method method, final Message<Envelop> message) {
        final Envelop envelop = message.body();
        final Class<?> returnType = method.getReturnType();
        final Class<?> tCls = returnType.getComponentType();
        this.getLogger().info(MessageFormat.format(MessageConstant.MSG_FUTURE, this.getClass(), returnType));
        if (Envelop.class == tCls) {
            final Future<Envelop> result = ReflectionUtils.invoke(proxy, method.getName(), envelop);
            result.onComplete(item -> message.reply(item.result()));
        } else {
            final Future tResult = ReflectionUtils.invoke(proxy, method.getName(), envelop);
            tResult.onComplete(AsyncUtils.toHandler(message));
        }
    }
}
