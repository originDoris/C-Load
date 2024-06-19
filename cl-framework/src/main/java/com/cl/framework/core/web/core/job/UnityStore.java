package com.cl.framework.core.web.core.job;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.JobStatus;
import com.cl.framework.core.enums.JobType;
import com.cl.framework.core.web.core.worker.Mission;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class UnityStore implements JobStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnityStore.class);

    private final transient JobReader reader = new CodeStore();

    private final transient JobStore store = new ExtensionStore();

    @Override
    public Set<Mission> fetch() {
        final Set<Mission> missions = this.reader.fetch()
                .stream()
                .filter(Mission::isReadOnly)
                .collect(Collectors.toSet());
        LOGGER.info(MessageFormat.format(MessageConstant.JOB_SCANNED, missions.size(), "Programming"));

        final Set<Mission> storage = this.store.fetch()
                .stream()
                .filter(mission -> !mission.isReadOnly())
                .collect(Collectors.toSet());
        LOGGER.info(MessageFormat.format(MessageConstant.JOB_SCANNED, storage.size(), "Dynamic/Stored"));

        final Set<Mission> result = new HashSet<>();
        result.addAll(missions);
        result.addAll(storage);


        result.stream()
                .filter(mission -> JobType.ONCE == mission.getType())
                .filter(mission -> JobStatus.STARTING == mission.getStatus())
                .forEach(mission -> mission.setStatus(JobStatus.STOPPED));

        JobPool.put(result);
        return result;
    }

    @Override
    public JobStore add(final Mission mission) {
        JobPool.save(mission);
        return this.store.add(mission);
    }

    @Override
    public Mission fetch(final String name) {
        return JobPool.get(name, () -> {
            Mission mission = this.reader.fetch(name);
            if (Objects.isNull(mission)) {
                mission = this.store.fetch(name);
            }
            return mission;
        });
    }

    @Override
    public JobStore remove(final Mission mission) {
        JobPool.remove(mission.getCode());
        return this.store.remove(mission);
    }

    @Override
    public JobStore update(final Mission mission) {
        JobPool.save(mission);
        return this.store.update(mission);
    }
}
