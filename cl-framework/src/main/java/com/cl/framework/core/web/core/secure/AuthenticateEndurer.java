package com.cl.framework.core.web.core.secure;

import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.web.core.route.aim.Answer;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.ext.web.RoutingContext;

import java.text.MessageFormat;

/**
 * 处理故障
 * @author xhz
 */
public class AuthenticateEndurer implements Handler<RoutingContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticateEndurer.class);

    private AuthenticateEndurer() {
    }

    public static Handler<RoutingContext> create() {
        return new AuthenticateEndurer();
    }

    @Override
    public void handle(final RoutingContext event) {
        if (event.failed()) {
            final Throwable ex = event.failure();
            if (ex instanceof WebException) {
                LOGGER.info(MessageFormat.format("Web Exception: {0} = {1}", ex.getClass().getName(), ex.getMessage()));
                final WebException error = (WebException) ex;
                Answer.reply(event, Envelop.failure(error));
            } else {
                LOGGER.info(MessageFormat.format("Exception: {0} = {1}", ex.getClass().getName(), ex.getMessage()));
                ex.printStackTrace();
                Answer.reply(event, Envelop.failure(ex));
            }
        } else {
            event.next();
        }
    }
}
