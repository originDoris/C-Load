package com.cl.framework.core.web.core.job.center;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.job.JobPool;
import com.cl.framework.core.web.core.worker.Mission;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.Promise;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

public class FixedAgha extends AbstractAgha {

    @Override
    public Single<Long> begin(final Mission mission) {
        return Single.defer(() -> {
            final long delay = this.getDelay(mission);
            this.moveOn(mission, true);
            return this.interval().startAt(delay, mission.getDuration(), (timeId) -> this.working(mission, () -> {
                CubeFn.itRepeat(2, () -> this.moveOn(mission, true));
            })).map(aLong -> {
                JobPool.mount(aLong, mission.getCode());
                this.getLogger().info(MessageFormat.format(MessageConstant.JOB_INTERVAL, mission.getCode(),
                        String.valueOf(delay), String.valueOf(mission.getDuration()), String.valueOf(aLong)));
                return aLong;
            });
        });
    }

    private long getDelay(final Mission mission) {
        final Instant end = mission.getInstant();
        final Instant start = Instant.now();
        final long delay = ChronoUnit.MILLIS.between(start, end);
        if (0 < delay) {
            this.getLogger().info(MessageFormat.format(MessageConstant.JOB_DELAY, mission.getCode(), String.valueOf(delay)));
        }
        return delay < 0 ? 0L : delay;
    }
}
