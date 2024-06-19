package com.cl.framework.core.web.core.route.axis;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.handler.CommonErrorHandler;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.utils.VerifierEventUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.route.differ.ModeSplitter;
import com.cl.framework.core.web.core.route.Hub;
import com.cl.framework.core.web.core.route.MediaHub;
import com.cl.framework.core.web.core.route.UriHub;
import com.cl.framework.core.web.core.route.aim.Aim;
import com.cl.framework.core.web.core.Pool;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.ext.web.Route;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;

import java.text.MessageFormat;
import java.util.Set;

/**
 * 将event 配置到路由上
 *
 * @author xhz
 */
public class EventAxis implements Axis<Router>{

    private static final Logger LOGGER = LoggerFactory.getLogger(EventAxis.class);


    private static final Set<Event> EVENTS = DataTableAnno.getEvents();

    private transient final ModeSplitter splitter = CubeFn.poolThread(Pool.THREADS, () -> ReflectionUtils.newInstance(ModeSplitter.class));


    @Override
    public void mount(final Router router) {
        EVENTS.forEach(event -> CubeFn.safeSemi(null == event,
                () -> LOGGER.warn(MessageFormat.format(MessageConstant.NULL_EVENT, this.getClass().getName())),
                () -> {
                    VerifierEventUtil.verify(event);

                    final Route route = router.route();
                    Hub<Route> hub = CubeFn.poolThread(Pool.URIHUBS, () -> ReflectionUtils.newInstance(UriHub.class));
                    hub.mount(route, event);
                    hub = CubeFn.poolThread(Pool.MEDIAHUBS, () -> ReflectionUtils.newInstance(MediaHub.class));
                    hub.mount(route, event);

                    final Aim<RoutingContext> aim = this.splitter.distribute(event);

                    route.failureHandler(CommonErrorHandler.create())
                            .handler(aim.attack(event))
                            .failureHandler(CommonErrorHandler.create());
                }, LOGGER));
    }
}
