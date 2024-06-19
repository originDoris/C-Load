package com.cl.framework.core.web.core.route.axis;


import com.cl.framework.core.constants.Orders;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.web.core.Pool;
import com.cl.framework.core.web.core.secure.AuthenticateEndurer;
import com.cl.framework.core.web.core.secure.Bolt;
import com.cl.framework.core.web.core.secure.Cliff;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.handler.AuthenticationHandler;
import io.vertx.rxjava3.ext.web.handler.ChainAuthHandler;

import java.util.Set;
import java.util.TreeSet;

/**
 * Secure mount
 * 401 授权使用
 * @author xhz
 */
public class WallAxis implements Axis<Router> {

    private static final Set<Cliff> WALLS = DataTableAnno.getWalls();

    static {
        WALLS.forEach(wall -> {
            if (!Pool.WALL_MAP.containsKey(wall.getPath())) {
                Pool.WALL_MAP.put(wall.getPath(), new TreeSet<>());
            }
            Pool.WALL_MAP.get(wall.getPath()).add(wall);
        });
    }

    private transient final Vertx vertx;
    private transient final Bolt bolt;

    public WallAxis(final Vertx vertx) {
        this.vertx = vertx;
        bolt = Bolt.get();
    }

    @Override
    public void mount(final Router router) {
        Pool.WALL_MAP.forEach((path, cliffes) -> {
            final AuthenticationHandler handler = create(vertx, cliffes);
            if (null != handler) {
                router.route(path).order(Orders.SECURE).handler(handler)
                        .failureHandler(AuthenticateEndurer.create());
            }
        });
    }

    private AuthenticationHandler create(final Vertx vertx, final Set<Cliff> cliffes) {
        AuthenticationHandler resultHandler = null;
        if (1 < cliffes.size()) {
            // 1 < handler
            final ChainAuthHandler chain = ChainAuthHandler.create();
            Observable.fromIterable(cliffes)
                    .map(item -> bolt.mount(vertx, item))
                    .subscribe(chain::add).dispose();
            resultHandler = chain;
        } else {
            // 1 = handler
            if (!cliffes.isEmpty()) {
                final Cliff cliff = cliffes.iterator().next();
                resultHandler = bolt.mount(vertx, cliff);
            }
        }
        return resultHandler;
    }
}
