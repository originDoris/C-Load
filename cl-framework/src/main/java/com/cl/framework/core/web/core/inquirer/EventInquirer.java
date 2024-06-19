package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.thread.EndPointThread;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 将EndPoint 转换为Event
 *
 * @author xhz
 */
@Slf4j
public class EventInquirer implements Inquirer<Set<Event>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventInquirer.class);
    @Override
    public Set<Event> scan(Set<Class<?>> endpoints) {
        final List<EndPointThread> threadReference = new ArrayList<>();
        for (final Class<?> endpoint : endpoints) {
            final EndPointThread thread = new EndPointThread(endpoint);
            threadReference.add(thread);
            thread.start();
        }
        CubeFn.safeJvm(() -> {
            for (final EndPointThread item : threadReference) {
                item.join();
            }
        }, LOGGER);
        final Set<Event> events = new HashSet<>();
        CubeFn.safeJvm(() -> {
            for (final EndPointThread item : threadReference) {
                events.addAll(item.getEvents());
            }
        }, LOGGER);
        return events;
    }
}
