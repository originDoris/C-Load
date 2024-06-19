package com.cl.framework.core.vertx;

import com.cl.framework.core.exception.ExceptionHandler;
import com.hazelcast.internal.util.CollectionUtil;
import io.vertx.rxjava3.ext.web.Route;
import io.vertx.rxjava3.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RouterHelper {

    /**
     * 加载多个路由配置
     */
    public static void handle(final List<RouterConfig> configList, final Router router) {
        if (CollectionUtil.isNotEmpty(configList)) {
            configList.forEach(routerConfig -> {
                routerConfig.init(router);
            });
        }
        router.route().last().failureHandler(ExceptionHandler.of());
    }
}
