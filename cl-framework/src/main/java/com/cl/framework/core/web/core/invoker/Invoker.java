package com.cl.framework.core.web.core.invoker;

import com.cl.framework.core.runtime.Envelop;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.eventbus.Message;

import java.lang.reflect.Method;

/**
 * 方法调用应答
 * @author xhz
 */
public interface Invoker {

    void ensure(final Class<?> returnType, final Class<?> paramCls);

    void invoke(Object proxy, Method method, Message<Envelop> message);

}
