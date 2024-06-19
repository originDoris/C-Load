package com.cl.framework.core.web.core.deployment;

import io.vertx.core.DeploymentOptions;

/**
 * 部署 Agent 或者Worker
 *
 * @author xhz
 */
public interface Rotate {

    DeploymentOptions spinAgent(Class<?> clazz);

    DeploymentOptions spinWorker(Class<?> clazz);
}
