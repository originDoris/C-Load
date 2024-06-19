package com.cl.framework.core.web.core.job;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.Vertx;


public interface JobClient {
    /*
     * Create local session store bind data
     */
    static JobClient createShared( final JsonObject config) {
        return new JobClientImpl(config);
    }

    static JobClient createShared() {
        return new JobClientImpl(new JsonObject());
    }

    /**
     * Start new job
     */
    @Fluent
    JobClient start(final String name, final Handler<AsyncResult<Long>> handler);

    /**
     * Stop running job
     */
    @Fluent
    JobClient stop(final Long timerId, final Handler<AsyncResult<Boolean>> handler);

    /**
     * Resume a failure job
     */
    @Fluent
    JobClient resume(final Long timeId, final Handler<AsyncResult<Long>> handler);
}
