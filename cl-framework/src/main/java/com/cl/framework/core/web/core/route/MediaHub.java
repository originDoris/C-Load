package com.cl.framework.core.web.core.route;


import com.cl.framework.core.web.core.agent.Event;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.rxjava3.ext.web.Route;

import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Producer/Consumer
 * @author xhz
 */
public class MediaHub implements Hub<Route> {

    @Override
    public void mount(final Route route,
                      final Event event) {
        // produces
        final Set<MediaType> produces = event.getProduces();
        Observable.fromIterable(produces)
                .map(type -> type.getType() + "/" + type.getSubtype())
                .subscribe(route::produces).dispose();
        // consumes
        final Set<MediaType> consumes = event.getProduces();
        Observable.fromIterable(consumes)
                .map(type -> type.getType() + "/"+ type.getSubtype())
                .subscribe(route::consumes).dispose();
    }
}
