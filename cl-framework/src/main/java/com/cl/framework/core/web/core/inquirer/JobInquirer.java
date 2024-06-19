package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.annotation.Job;
import com.cl.framework.core.constants.DefaultConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.JobStatus;
import com.cl.framework.core.enums.JobType;
import com.cl.framework.core.utils.StringUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.worker.Mission;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JobInquirer
 * @author xhz
 */
public class JobInquirer implements Inquirer<Set<Mission>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobInquirer.class);

    @Override
    public Set<Mission> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> jobs = clazzes.stream()
                .filter(item -> item.isAnnotationPresent(Job.class))
                .collect(Collectors.toSet());
        LOGGER.info(MessageFormat.format(MessageConstant.SCANED_JOB, jobs.size()));
        return jobs.stream().map(this::initialize)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Mission initialize(final Class<?> clazz) {
        final Annotation annotation = clazz.getAnnotation(Job.class);
        if (Objects.isNull(annotation)) {
            return null;
        }
        final JobType type = ReflectionUtils.invoke(annotation, "value");

        String name = ReflectionUtils.invoke(annotation, "name");
        name = StringUtils.isBlank(name) ? clazz.getName() : name;

        final Mission mission = new Mission();

        mission.setName(name);
        mission.setReadOnly(Boolean.TRUE);
        mission.setType(type);
        mission.setStatus(JobStatus.STARTING);

        if (-1 == mission.getDuration()) {
            mission.setDuration(this.time(annotation, "duration", "durationUnit"));
        }
        if (-1 == mission.getThreshold()) {
            mission.setThreshold(this.time(annotation, "threshold", "thresholdUnit"));
        }
        if (StringUtils.isBlank(mission.getCode())) {
            mission.setCode(DefaultConstant.DEFAULT_JOB_NAMESPACE + "-" + mission.getName());
        }
        mission.connect(clazz);
        if (Objects.isNull(mission.getOn())) {
            LOGGER.warn(MessageFormat.format(MessageConstant.JOB_IGNORE, clazz.getName()));
            return null;
        }
        return mission;
    }


    private long time(final Annotation annotation, final String field, final String fieldUnit) {
        final long duration = ReflectionUtils.invoke(annotation, field);
        final TimeUnit timeUnit = ReflectionUtils.invoke(annotation, fieldUnit);
        return timeUnit.toMillis(duration);
    }

}
