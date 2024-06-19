package com.cl.framework.core.web.core.filter;


import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.http.Cookie;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.Session;

import java.util.Set;

public abstract class HttpFilter implements Filter {

    private transient final Logger logger = LoggerFactory.getLogger(this.getClass());
    private transient RoutingContext context;

    @Override
    public void init(final RoutingContext context) {
        this.context = context;
        this.init();
    }

    protected void put(final String key, final Object value) {
        this.context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    protected <T> T get(final String key) {
        final Object reference = this.context.get(key);
        return null == reference ? null : (T) reference;
    }

    protected void doNext(final HttpServerRequest request,
                          final HttpServerResponse response) {
        // If response end it means that it's not needed to move next.
        if (!response.ended()) {
            this.context.next();
        }
    }

    protected Session getSession() {
        return this.context.session();
    }

    protected Set<Cookie> getCookies() {
        return this.context.request().cookies();
    }

    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    public void init() {
        CubeFn.outError(this.logger,null == this.context, DataTableException.class, ErrorCodeEnum.WALL_DUPLICATED_ERROR);
    }
}
