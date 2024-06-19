package com.cl.framework.core.web.core.job;

import com.cl.framework.core.web.core.job.center.Agha;
import com.cl.framework.core.web.core.worker.Mission;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.Vertx;


import java.text.MessageFormat;
import java.util.Objects;

public class JobClientImpl implements JobClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobClientImpl.class);
    private transient final JsonObject config;

    JobClientImpl( final JsonObject config) {
        this.config = config;
    }

    @Override
    public JobClient start(final String code, final Handler<AsyncResult<Long>> handler) {
        final Mission mission = JobPool.get(code);
        if (Objects.nonNull(mission)) {
            final Agha agha = Agha.get(mission.getType());
            final Single<Long> future = agha.begin(mission);
            future.subscribe(trimId -> {
                handler.handle(Future.succeededFuture(trimId));
            });
        } else {
            LOGGER.info(MessageFormat.format("( JobClient ) The pool could not find job of code = `{0}`", code));
        }
        return this;
    }

    @Override
    public JobClient stop(final Long timerId, final Handler<AsyncResult<Boolean>> handler) {
        JobPool.stop(timerId);
        handler.handle(Future.succeededFuture(Boolean.TRUE));
        Vertx.vertx().cancelTimer(timerId);
        return this;
    }

    @Override
    public JobClient resume(final Long timeId, final Handler<AsyncResult<Long>> handler) {
        JobPool.resume(timeId);
        final String code = JobPool.code(timeId);
        return this.start(code, handler);
    }
}
