package com.cl.framework.core.web.core.invoker;

import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.rxjava3.core.eventbus.Message;

import java.lang.reflect.Method;

/**
 * void method(Messsage<Envelop>)
 * @author xhz
 */
public class MessageInvoker extends AbstractInvoker {
    @Override
    public void ensure(final Class<?> returnType, final Class<?> paramCls) {
        final boolean valid = (void.class == returnType || Void.class == returnType) && Message.class.isAssignableFrom(paramCls);
        InvokerUtil.verify(!valid, returnType, paramCls, this.getClass());
    }

    @Override
    public void invoke(final Object proxy, final Method method, final Message<Envelop> message) {
        ReflectionUtils.invoke(proxy, method.getName(), message);
    }

}
