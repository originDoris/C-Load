package com.cl.framework.core.web.core.worker;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.Extractor;
import com.cl.framework.core.web.core.deployment.DeployRotate;
import com.cl.framework.core.web.core.deployment.Rotate;


import io.vertx.core.DeploymentOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Worker verticle deployment
 * @author xhz
 */
public class WorkerExtractor implements Extractor<DeploymentOptions> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerExtractor.class);

    private static final ConcurrentMap<Class<?>, DeploymentOptions>
            OPTIONS = new ConcurrentHashMap<>();

    @Override
    public DeploymentOptions extract(final Class<?> clazz) {
        CubeFn.safeNull(() -> LOGGER.info(MessageFormat.format(MessageConstant.WORKER_HIT, clazz.getName())), clazz);
        final Rotate rotate = ReflectionUtils.singleton(DeployRotate.class);

        return CubeFn.pool(OPTIONS, clazz, () -> rotate.spinWorker(clazz));
    }
}
