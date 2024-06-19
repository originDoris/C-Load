package com.cl.framework.core.web.core.job.timer;

import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Handler;

/**
 * @author xhz
 */
public interface Interval {
    Single<Long> startAt(long delay, long duration, Handler<Long> actuator);

    Single<Long> startAt(long duration, Handler<Long> actuator);

    Single<Long> startAt(Handler<Long> actuator);
}
