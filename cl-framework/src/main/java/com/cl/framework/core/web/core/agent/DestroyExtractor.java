package com.cl.framework.core.web.core.agent;

import com.cl.framework.core.annotation.*;
import com.cl.framework.core.web.core.Extractor;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 扫描 @Destroy 类构建Event元数据
 *
 * @author xhz
 */
@Slf4j

public class DestroyExtractor implements Extractor<Set<Hook>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DestroyExtractor.class);

    @Override
    public Set<Hook> extract(final Class<?> clazz) {
        final Set<Hook> result = new ConcurrentHashSet<>();
        if (clazz.isAnnotationPresent(Destroy.class)) {
            final Destroy initial = Anno.getDestroy(clazz);
            assert null != initial : "initial should not be null.";
            result.addAll(pares(clazz));
        }
        return result;
    }

    private Set<Hook> pares(final Class<?> clazz) {
        Set<Hook> hooks = new HashSet<>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(DoDestroy.class)) {
                Hook hook = new Hook();
                hook.setAction(declaredMethod);
                hook.setAClass(clazz);
                if (declaredMethod.isAnnotationPresent(Adjust.class)) {
                    Adjust annotation = declaredMethod.getAnnotation(Adjust.class);
                    int value = annotation.value();
                    hook.setOrder(value);
                }
                hooks.add(hook);
            }
        }
        return hooks;
    }

}
