package com.cl.framework.core.web.core.deployment;

import com.cl.framework.core.annotation.Agent;
import com.cl.framework.core.annotation.Worker;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;

/**
 * 表达式解析器
 *
 * @author xhz
 */
public class DeployRotate implements Rotate{

    public static final String TYPE = "type";

    public static final  String INSTANCES = "instances";

    public static final String HA = "ha";

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTableAnno.class);

    @Override
    public DeploymentOptions spinAgent(final Class<?> clazz) {
        final Annotation annotation = clazz.getDeclaredAnnotation(Agent.class);
        final DeploymentOptions options = spinOpts(annotation);
        options.setWorker(false);

        LOGGER.info(MessageFormat.format(MessageConstant.VTC_OPT, options.getInstances(), options.toJson()));
        return options;
    }

    @Override
    public DeploymentOptions spinWorker(final Class<?> clazz) {
        final Annotation annotation = clazz.getDeclaredAnnotation(Worker.class);
        final DeploymentOptions options = spinOpts(annotation);

        options.setWorker(true);

        LOGGER.info(MessageFormat.format(MessageConstant.VTC_OPT, options.getInstances(), options.toJson()));
        return options;
    }

    private DeploymentOptions spinOpts(final Annotation annotation) {
        final int instances = ReflectionUtils.invoke(annotation, INSTANCES);
        final boolean ha = ReflectionUtils.invoke(annotation, HA);
        final DeploymentOptions options = new DeploymentOptions();
        options.setHa(ha);
        options.setInstances(instances);
        return options;
    }
}
