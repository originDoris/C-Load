package com.cl.framework.core.web.core.job.center;


import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.JobStatus;
import com.cl.framework.core.funcation.Actuator;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.vertx.VertxLauncher;
import com.cl.framework.core.web.core.job.JobConfig;
import com.cl.framework.core.web.core.job.JobPin;
import com.cl.framework.core.web.core.job.JobStore;
import com.cl.framework.core.web.core.job.phase.Phase;
import com.cl.framework.core.web.core.job.timer.Interval;
import com.cl.framework.core.web.core.worker.Mission;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.WorkerExecutor;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The chain should be
 *
 * 1. 输入数据来自 incomeAddress
 * 2. 如果incomeComponent 存在则触发
 * 3. 必须有component 并且包含主要逻辑
 * 4. 如果outcomeComponent存在则触发
 * 5. 将结果消息发送到 outcomeAddress
 * 6. 可以有一个callback async方法用于回调执行 如果 outcomeAddress ，数据来自事件总线，否则，数据来自 outcomeComponent
 */
public abstract class AbstractAgha implements Agha {

    private static final JobConfig CONFIG = JobPin.getConfig();
    private static final AtomicBoolean SELECTED = new AtomicBoolean(Boolean.TRUE);

    /**
     * STARTING ------|
     *                v
     *     |------> READY <-------------------|
     *     |          |                       |
     *     |          |                    <start>
     *     |          |                       |
     *     |        <start>                   |
     *     |          |                       |
     *     |          V                       |
     *     |        RUNNING --- <stop> ---> STOPPED
     *     |          |
     *     |          |
     *  <resume>   ( error )
     *     |          |
     *     |          |
     *     |          v
     *     |------- ERROR
     *
     */
    private static final ConcurrentMap<JobStatus, JobStatus> MOVING = new ConcurrentHashMap<JobStatus, JobStatus>() {
        {
            this.put(JobStatus.STARTING, JobStatus.READY);
            this.put(JobStatus.READY, JobStatus.RUNNING);
            this.put(JobStatus.RUNNING, JobStatus.STOPPED);
            this.put(JobStatus.STOPPED, JobStatus.READY);
            this.put(JobStatus.ERROR, JobStatus.READY);
        }
    };


    Interval interval() {
        final Class<?> intervalCls = CONFIG.getInterval().getComponent();
        final Interval interval = (Interval) ReflectionUtils.singleton(intervalCls);

        if (SELECTED.getAndSet(Boolean.FALSE)) {
            this.getLogger().info(MessageFormat.format(MessageConstant.JOB_COMPONENT_SELECTED, "Interval", interval.getClass().getName()));
        }
        return interval;
    }

    JobStore store() {
        return JobPin.getStore();
    }

    private Single<Envelop> workingAsync(final Mission mission) {

        final Phase phase = Phase.start(mission.getCode())
                .bind(VertxLauncher.getVertx())
                .bind(mission);

        return phase.inputAsync(mission)
                .flatMap(phase::incomeAsync)
                .flatMap(phase::invokeAsync)
                .flatMap(phase::outcomeAsync)
                .flatMap(phase::outputAsync)
                .flatMap(phase::callbackAsync);
    }

    void working(final Mission mission, final Actuator actuator) {
        if (JobStatus.READY == mission.getStatus()) {
            this.moveOn(mission, true);
            long threshold = mission.getThreshold();
            if (-1 == threshold) {
                threshold = TimeUnit.MINUTES.toNanos(5);
            }
            final String code = mission.getCode();
            final WorkerExecutor executor = VertxLauncher.getVertx().createSharedWorkerExecutor(code, 1, threshold);
            this.getLogger().info(MessageFormat.format(MessageConstant.JOB_POOL_START, code, String.valueOf(TimeUnit.NANOSECONDS.toSeconds(threshold))));

            executor.executeBlocking(() -> {
                return this.workingAsync(mission)
                        .flatMap(envelop -> {
                            actuator.execute();
                            this.getLogger().info(MessageFormat.format(MessageConstant.JOB_POOL_END, code));
                            return Single.just(envelop);
                        }).onErrorReturn(throwable -> {
                            if (!(throwable instanceof NoStackTraceThrowable)) {
                                throwable.printStackTrace();
                                this.moveOn(mission, false);
                            }
                            return Envelop.failure(throwable);
                        }).doFinally(()->{
                            executor.close().subscribe();
                        }).blockingGet();
            }).result();

        }
    }

    void moveOn(final Mission mission, final boolean noError) {
        if (noError) {
            if (MOVING.containsKey(mission.getStatus())) {
                final JobStatus moved = MOVING.get(mission.getStatus());
                final JobStatus original = mission.getStatus();
                mission.setStatus(moved);

                this.getLogger().info(MessageFormat.format(MessageConstant.JOB_MOVED, mission.getType(), mission.getCode(), original, moved));
                this.store().update(mission);
            }
        } else {
            if (JobStatus.RUNNING == mission.getStatus()) {
                mission.setStatus(JobStatus.ERROR);
                this.getLogger().info(MessageFormat.format(MessageConstant.JOB_TERMINAL, mission.getCode()));
                this.store().update(mission);
            }
        }
    }

    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }
}
