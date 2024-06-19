package com.cl.framework.core.web.core.inquirer;


import com.cl.framework.core.annotation.Di;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.web.core.thread.AffluxThread;
import com.cl.framework.plugin.Plugins;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 依赖注入
 * @author xhz
 */
public class AffluxInquirer implements Inquirer<ConcurrentMap<Class<?>, ConcurrentMap<String, Class<?>>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AffluxInquirer.class);

    @Override
    public ConcurrentMap<Class<?>, ConcurrentMap<String, Class<?>>> scan(final Set<Class<?>> classes) {

        final Set<Class<?>> enabled = classes.stream()
                .filter(item -> isMark(item, Plugins.INJECT_ANNOTATIONS))
                .collect(Collectors.toSet());
        final List<AffluxThread> threadReference = new ArrayList<>();
        for (final Class<?> clazz : enabled) {
            final AffluxThread thread = new AffluxThread(clazz, classes);
            threadReference.add(thread);
            thread.start();
        }
        threadReference.forEach(thread -> {
            try {
                thread.join();
            } catch (final InterruptedException ex) {
                LOGGER.info(ex);
            }
        });
        final ConcurrentMap<Class<?>, ConcurrentMap<String, Class<?>>> affluxes = new ConcurrentHashMap<>();
        for (final AffluxThread thread : threadReference) {
            if (!thread.isEmpty()) {
                final Class<?> key = thread.getClassKey();
                final ConcurrentMap<String, Class<?>> fields = thread.getFieldMap();
                affluxes.put(key, fields);
                LOGGER.info(MessageFormat.format(MessageConstant.SCANNED_INJECTION, key.getName(), fields.size()));
            }
        }
        return affluxes;
    }

    public static boolean isMark(final Class<?> clazz,
                                 final Set<Class<? extends Annotation>> annoCls) {
        return Arrays.stream(clazz.getDeclaredFields()).anyMatch(field -> annoCls.stream().anyMatch(field::isAnnotationPresent)) || clazz.isAnnotationPresent(Di.class);
    }
}
