package com.cl.framework.core.web.core.route.aim;


import com.cl.framework.core.annotation.Address;
import com.cl.framework.core.constants.ParamIdConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.funcation.Actuator;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.route.parse.Analyzer;
import com.cl.framework.core.web.core.route.parse.MediaAnalyzer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.eventbus.Message;
import io.vertx.rxjava3.ext.web.RoutingContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * 模板方法基类
 * @author xhz
 */
public abstract class BaseAim {

    private transient final Analyzer analyzer = ReflectionUtils.singleton(MediaAnalyzer.class);


    protected Object[] buildArgs(final RoutingContext context,
                                 final Event event) {
        Object[] cached = context.get(ParamIdConstant.PARAMS_CONTENT);
        if (null == cached) {
            cached = this.analyzer.in(context, event);
            context.put(ParamIdConstant.PARAMS_CONTENT, cached);
        }
        return cached;
    }


    protected String address(final Event event) {
        final Method method = event.getAction();
        final Annotation annotation = method.getDeclaredAnnotation(Address.class);
        return ReflectionUtils.invoke(annotation, "value");
    }

    protected Object invoke(final Event event, final Object[] args) {
        final Method method = event.getAction();
        this.getLogger().info(MessageFormat.format("Class = {2}, Method = {0}, Args = {1}", method.getName(), FieldUtil.fromJoin(args,","), method.getDeclaringClass().getName()));
        return ReflectionUtils.invoke(event.getProxy(), method.getName(), args);
    }

    protected Envelop failure(final String address,
                              final Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
            WebException webException = new WebException(ErrorCodeEnum.INTERNAL_ERROR, "address: " + address + "," + throwable.getMessage());
            return Envelop.failure(webException);
        }
        return Envelop.failure(new WebException(ErrorCodeEnum.INTERNAL_ERROR));
    }

    protected Envelop success(final String address, final Message<Envelop> message) {
        Envelop envelop;
        try {
            envelop = message.body();
        } catch (final Throwable ex) {
            WebException webException = new WebException(ErrorCodeEnum.INTERNAL_ERROR,ex.getMessage());
            envelop = Envelop.failure(webException);
        }
        return envelop;
    }


    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }


    protected void exec(final Actuator consumer,
                        final RoutingContext context,
                        final Event event) {
        try {
            consumer.execute();
        } catch (Exception e) {
            final Envelop envelop = Envelop.failure(e);
            Answer.reply(context, envelop, event);
        }
    }

    protected DeliveryOptions delivery() {
        final DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(600000);
        return options;
    }
}
