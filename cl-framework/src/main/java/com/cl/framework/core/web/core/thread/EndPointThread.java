package com.cl.framework.core.web.core.thread;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.agent.EventExtractor;
import com.cl.framework.core.web.core.Extractor;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * 扫描每一个EndPoint转换为Event
 *
 * @author xhz
 */
@Slf4j
public class EndPointThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndPointThread.class);

    @Getter
    private final Set<Event> events = new HashSet<>();

    private final transient Extractor<Set<Event>> extractor = ReflectionUtils.newInstance(EventExtractor.class);

    private final transient Class<?> reference;

    public EndPointThread(final Class<?> clazz) {
        this.setName("cl.endpoint-scanner-" + this.getId());
        this.reference = clazz;
    }

    @Override
    public void run() {
        if (null != this.reference) {
            this.events.addAll(this.extractor.extract(this.reference));
            LOGGER.info(MessageFormat.format(MessageConstant.SCANNED_EVENTS, this.reference.getName(), this.events.size()));
        }
    }

}
