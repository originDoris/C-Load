package com.cl.framework.core.web.core.invoker;


import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.runtime.Envelop;
import io.vertx.rxjava3.core.eventbus.Message;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * DynamicInvoker
 * @author xhz
 */
public class DynamicInvoker extends AbstractInvoker {

    @Override
    public void ensure(final Class<?> returnType, final Class<?> paramCls) {
        final boolean valid = (void.class != returnType && Void.class != returnType);
        InvokerUtil.verify(!valid, returnType, paramCls, this.getClass());
    }

    @Override
    public void invoke(final Object proxy, final Method method, final Message<Envelop> message) {
        final Envelop envelop = message.body();
        this.getLogger().info(MessageFormat.format(MessageConstant.MSG_FUTURE, this.getClass(), method.getReturnType()));
        final Object returnValue = this.invokeInternal(proxy, method, envelop);
        message.reply(Envelop.success(returnValue));
    }

}
