package com.cl.framework.core.utils;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.funcation.CubeFn;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.rxjava3.core.Vertx;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 部署Verticle工具类
 *
 * @author xhz
 */
public class VerticleUtils {

    private static final ConcurrentMap<Class<?>, String> INSTANCES = new ConcurrentHashMap<>();

    public static void deploy(final Vertx vertx,
                              final Class<?> clazz,
                              final DeploymentOptions option,
                              final Logger logger) {

        final String name = clazz.getName();
        final String flag = option.isWorker() ? "Worker" : "Agent";

        Disposable subscribe = vertx.deployVerticle(name, option).subscribe((s, throwable) -> {
            if (throwable == null) {
                logger.info(MessageFormat.format(MessageConstant.VTC_END, name, option.getInstances(), true, flag));
                INSTANCES.put(clazz, s);
            } else {
                throwable.printStackTrace();
                logger.warn(MessageFormat.format(MessageConstant.VTC_FAIL, name, option.getInstances(), throwable, throwable.getMessage()));
            }
        });
    }

    public static void undeploy(final Vertx vertx,
                         final Class<?> clazz,
                         final DeploymentOptions option,
                         final Logger logger) {
        final String name = clazz.getName();
        final String flag = option.isWorker() ? "Worker" : "Agent";
        final String id = INSTANCES.get(clazz);

        CubeFn.safeNull(() -> vertx.undeploy(id).subscribe(() -> logger.info(MessageFormat.format(MessageConstant.VTC_STOPPED, name, id, flag))).dispose(), id);
    }
}
