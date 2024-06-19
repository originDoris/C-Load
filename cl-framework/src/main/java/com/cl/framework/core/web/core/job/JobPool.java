package com.cl.framework.core.web.core.job;


import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.JobStatus;
import com.cl.framework.core.utils.StringUtil;
import com.cl.framework.core.web.core.worker.Mission;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 存储job的池
 * @author xhz
 */
public class JobPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobPool.class);

    private static final ConcurrentMap<String, Mission> JOBS = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Long, String> RUNNING = new ConcurrentHashMap<>();

    public static ConcurrentMap<String, Mission> mapJobs() {
        return JOBS;
    }

    public static ConcurrentMap<Long, String> mapRuns() {
        return RUNNING;
    }

    public static void put(final Set<Mission> missions) {
        missions.forEach(mission -> JOBS.put(mission.getCode(), mission));
    }

    public static Mission get(final String code, final Supplier<Mission> supplier) {
        return JOBS.getOrDefault(code, supplier.get());
    }

    public static Mission get(final String code) {
        return JOBS.get(code);
    }

    public static List<Mission> get() {
        return JOBS.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static String code(final Long timeId) {
        return RUNNING.get(timeId);
    }

    public static Long timeId(final String code) {
        return RUNNING.keySet().stream()
                .filter(key -> code.equals(RUNNING.get(key)))
                .findFirst().orElse(null);
    }

    public static void remove(final String code) {
        JOBS.remove(code);
    }

    public static void save(final Mission mission) {
        JOBS.put(mission.getCode(), mission);
    }

    public static boolean valid(final Mission mission) {
        return JOBS.containsKey(mission.getCode());
    }

    public static void mount(final Long timeId, final String code) {
        RUNNING.put(timeId, code);
    }

    public static void start(final Long timeId, final String code) {
        uniform(code, mission -> {
            final JobStatus status = mission.getStatus();
            if (JobStatus.RUNNING == status) {
                LOGGER.info(MessageFormat.format(MessageConstant.IS_RUNNING, code));
            } else if (JobStatus.ERROR == status) {
                LOGGER.warn(MessageFormat.format(MessageConstant.IS_ERROR, code));
            } else if (JobStatus.STARTING == status) {
                LOGGER.warn(MessageFormat.format(MessageConstant.IS_STARTING, code));
            } else {
                if (JobStatus.STOPPED == status) {
                    JOBS.get(code).setStatus(JobStatus.READY);
                }
                RUNNING.put(timeId, code);

            }
        });
    }

    /*
     * Stop job
     * -->
     */
    public static void stop(final Long timeId) {
        uniform(timeId, mission -> {
            final JobStatus status = mission.getStatus();
            if (JobStatus.RUNNING == status || JobStatus.READY == status) {
                RUNNING.remove(timeId);
                mission.setStatus(JobStatus.STOPPED);
            } else {
                LOGGER.info(MessageFormat.format(MessageConstant.NOT_RUNNING, mission.getCode(), status));
            }
        });
    }

    public static void resume(final Long timeId) {
        uniform(timeId, mission -> {
            final JobStatus status = mission.getStatus();
            if (JobStatus.ERROR == status) {
                /*
                 * If `ERROR`
                 * resume will be triggered
                 * ERROR -> READY
                 */
                RUNNING.put(timeId, mission.getCode());
                mission.setStatus(JobStatus.READY);
            }
        });
    }

    private static void uniform(final Long timeId, final Consumer<Mission> consumer) {
        final String code = RUNNING.get(timeId);
        if (StringUtils.isBlank(code)) {
            LOGGER.info(MessageFormat.format(MessageConstant.IS_STOPPED, timeId));
        } else {
            uniform(code, consumer);
        }
    }

    private static void uniform(final String code, final Consumer<Mission> consumer) {
        final Mission mission = JOBS.get(code);
        if (Objects.nonNull(mission)) {
            consumer.accept(mission);
        }
    }
}
