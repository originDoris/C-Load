package com.cl.framework.core.web.core.job.center;


import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.web.core.job.JobPool;
import com.cl.framework.core.web.core.worker.Mission;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.Promise;

import java.text.MessageFormat;

/**
 * Start one time
 */
public class OnceAgha extends AbstractAgha {

    @Override
    public Single<Long> begin(final Mission mission) {
        return Single.defer(() -> {
            this.moveOn(mission, true);

            return this.interval().startAt((timeId) -> this.working(mission, () -> {
                this.moveOn(mission, true);
            })).flatMap(jobId -> {
                JobPool.mount(jobId, mission.getCode());
                this.getLogger().info(MessageFormat.format(MessageConstant.JOB_INTERVAL, mission.getCode(),
                        String.valueOf(0), String.valueOf(-1), String.valueOf(jobId)));
                return Single.just(jobId);
            });
        });


    }
}
