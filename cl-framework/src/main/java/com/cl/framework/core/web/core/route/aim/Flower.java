package com.cl.framework.core.web.core.route.aim;

import com.cl.framework.core.runtime.Envelop;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.ext.web.RoutingContext;

class Flower {

    private static final Logger LOGGER = LoggerFactory.getLogger(Flower.class);

    static <T> Single<Envelop> next(final RoutingContext context,
                                    final T entity) {
        return Single.just(Envelop.success(entity).bind(context));

    }

}
