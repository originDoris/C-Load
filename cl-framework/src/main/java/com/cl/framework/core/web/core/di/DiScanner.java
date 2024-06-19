package com.cl.framework.core.web.core.di;


import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.Pool;
import com.cl.framework.plugin.Plugins;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 扫描依赖注入
 *
 * @author xhz
 */
public class DiScanner {

    private static final ConcurrentMap<Class<?>, ConcurrentMap<String, Class<?>>> PENDINGS = com.cl.framework.core.runtime.DataTableAnno.getPlugins();

    private static final DiAnchor INJECTOR = new DiAnchor(DiScanner.class);

    private final transient Logger logger;


    private static final ConcurrentMap<Class<?>, Object> SINGLETON_OBJECTS = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Class<?>, Object> EARLY_SINGLETON_OBJECTS = new ConcurrentHashMap<>();

    private DiScanner(final Class<?> callee) {
        this.logger = LoggerFactory.getLogger(callee);
    }

    public static DiScanner create(final Class<?> callee) {
        return CubeFn.pool(Pool.INJECTION, callee, () -> new DiScanner(callee));
    }

    @SuppressWarnings("all")
    public <T> T singleton(final Class<?> instanceCls) {
        final Object instance = ReflectionUtils.singleton(instanceCls);
        if (Objects.nonNull(instance)) {
            this.singleton(instance);
        }
        return (T) instance;
    }

    public void singleton(final Object instance) {
        final Class<?> clazz = instance.getClass();
        if (PENDINGS.containsKey(clazz)) {
            final ConcurrentMap<String, Class<?>> injections = PENDINGS.getOrDefault(clazz, new ConcurrentHashMap<>());
            injections.forEach((fieldName, type) -> this.singleton(instance, fieldName, type));
            SINGLETON_OBJECTS.put(instance.getClass(), instance);
            EARLY_SINGLETON_OBJECTS.remove(instance.getClass());
        }
    }

    private void singleton(final Object proxy, final String fieldName, final Class<?> type) {
        try {
            final Class<?> clazz = proxy.getClass();
            final Field field = ReflectionUtils.getField(clazz, fieldName);
            final Object next;
            if (Plugins.INFIX_MAP.keySet().stream().anyMatch(field::isAnnotationPresent)) {
                next = INJECTOR.initialize(field);
            } else {
                if (SINGLETON_OBJECTS.containsKey(type)) {
                    next = SINGLETON_OBJECTS.get(type);
                    ReflectionUtils.set(proxy, fieldName, next);
                    return;
                } else if (EARLY_SINGLETON_OBJECTS.containsKey(type)) {
                    next = EARLY_SINGLETON_OBJECTS.get(type);
                    ReflectionUtils.set(proxy, fieldName, next);
                    return;
                }else {
                    next = ReflectionUtils.singleton(type);
                }
            }

            if (Objects.nonNull(next)) {
//                DependencyGraph.addDependency(proxy.getClass().getName(), type.getName());
//                DependencyGraph.hasCycle();
                ReflectionUtils.set(proxy, fieldName, next);
                EARLY_SINGLETON_OBJECTS.put(type, next);
                this.singleton(next);
            }
        } catch (final NoSuchFieldException ex) {
            logger.info(ex);
        }
    }
}
