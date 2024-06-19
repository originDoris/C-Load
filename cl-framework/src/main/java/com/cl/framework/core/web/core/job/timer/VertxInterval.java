package com.cl.framework.core.web.core.job.timer;


import com.cl.framework.core.vertx.VertxLauncher;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Handler;


/**
 * @author xhz
 */
public class VertxInterval implements Interval {
    private static final int START_UP_MS = 1;


    @Override
    public Single<Long> startAt(final long delay, final long duration, final Handler<Long> actuator) {

        return Single.just(VertxLauncher.getVertx().setTimer(delay + START_UP_MS, ignored -> {
                    VertxLauncher.getVertx().setPeriodic(START_UP_MS + duration, actuator);
                }
        ));
    }

    @Override
    public Single<Long> startAt(final long duration, final Handler<Long> actuator) {
        return Single.just(VertxLauncher.getVertx().setPeriodic(START_UP_MS + duration, actuator));
    }

    @Override
    public Single<Long> startAt(final Handler<Long> handler) {
        return Single.just(VertxLauncher.getVertx().setTimer(START_UP_MS, handler));
    }
}
