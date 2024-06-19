package com.cl.framework.core.web.core.job;


import com.cl.framework.core.annotation.Worker;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.JobType;
import com.cl.framework.core.web.core.job.center.Agha;
import com.cl.framework.core.web.core.worker.Mission;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.AbstractVerticle;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;

/**
 * 用来进行任务调度，部署所有的任务，必须只有1个实例 否则会有重复调度的问题
 * @author xhz
 */
@Worker(instances = 1)
public class DataTableScheduler extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTableScheduler.class);
    private static final JobStore STORE = JobPin.getStore();

    public DataTableScheduler() {
    }

    @Override
    public void start() {
        final JobConfig config = JobPin.getConfig();
        if (Objects.nonNull(config)) {
            final Set<Mission> missions = STORE.fetch();
            if (missions.isEmpty()) {
                LOGGER.info(MessageConstant.JOB_EMPTY);
            } else {
                LOGGER.info(MessageFormat.format(MessageConstant.JOB_MONITOR, missions.size()));
                missions.forEach(this::start);
            }
        } else {
            LOGGER.info(MessageConstant.JOB_CONFIG_NULL);
        }
    }

    private void start(final Mission mission) {
        final Agha agha = Agha.get(mission.getType());
        if (Objects.nonNull(agha)) {
            LOGGER.info(MessageFormat.format(MessageConstant.JOB_AGHA_SELECTED, agha.getClass(), mission.getCode(), mission.getType()));
            if (JobType.ONCE != mission.getType()) {
                agha.begin(mission).subscribe();
            }
        }
    }
}
