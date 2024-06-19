package com.cl.framework.core.vertx;

import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.rxjava3.ext.web.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：路由器全局对象持有者
 */
public class RouterHolder {
    private static Router instance ;

    public static Router getRxRouterInstance() {
        if (instance == null) {
            instance = Router.router(VertxLauncher.getVertx());
        }
        return instance;
    }
    public static io.vertx.ext.web.Router getRouterInstance() {
        return instance.getDelegate();
    }


    public static List<RouterConfig> getAllRouter(String packageName) {
        List<Class<? extends RouterConfig>> classes = ReflectionUtils.getClasses(packageName, RouterConfig.class);
        ArrayList<RouterConfig> routerConfigs = new ArrayList<>();
        for (Class<? extends RouterConfig> superType : classes) {
            RouterConfig config = ReflectionUtils.newInstance(superType);
            routerConfigs.add(config);
        }
        return routerConfigs;
    }
}
