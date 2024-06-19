package com.cl.framework.core.web.core.di;

import com.cl.framework.core.annotation.Plugin;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.Pool;
import com.cl.framework.plugin.Infix;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("all")
public class DiPlugin {

    private transient final Class<?> clazz;
    private transient final Logger logger;

    private DiPlugin(final Class<?> clazz) {
        this.clazz = clazz;
        logger = LoggerFactory.getLogger(clazz);
    }

    public static DiPlugin create(final Class<?> clazz) {
        return CubeFn.pool(Pool.PLUGINS, clazz, () -> new DiPlugin(clazz));
    }

    public void inject(final Object proxy) {
        final ConcurrentMap<Class<?>, Class<?>> binds = getBind();
        final Class<?> type = proxy.getClass();
        Observable.fromArray(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Plugin.class))
                .subscribe(field -> {
                    final Class<?> fieldType = field.getType();
                    final Class<?> infixCls = binds.get(fieldType);
                    if (null != infixCls) {
                        if (ReflectionUtils.isMatch(infixCls, Infix.class)) {
                            final Infix reference = (Infix)ReflectionUtils.singleton(infixCls);
                            final Object tpRef = ReflectionUtils.invoke(reference, "get");
                            final String fieldName = field.getName();
                            ReflectionUtils.set(proxy, fieldName, tpRef);
                        } else {
                            logger.warn(MessageFormat.format(MessageConstant.INFIX_IMPL, infixCls.getName(), Infix.class.getName()));
                        }
                    } else {
                        logger.warn(MessageFormat.format(MessageConstant.INFIX_NULL, field.getType().getName(), field.getName(), type.getName()));
                    }
                })
                .dispose();
    }

    private ConcurrentMap<Class<?>, Class<?>> getBind() {
        final Set<Class<?>> infixes = new HashSet<>(com.cl.framework.core.runtime.DataTableAmbient.getInjections().values());
        final ConcurrentMap<Class<?>, Class<?>> binds = new ConcurrentHashMap<>();
        Observable.fromIterable(infixes)
                .filter(Infix.class::isAssignableFrom)
                .subscribe(item -> {
                    final Method method = CubeFn.safeDefault(null, () -> item.getDeclaredMethod("get"), item);
                    final Class<?> type = method.getReturnType();
                    binds.put(type, item);
                })
                .dispose();
        return binds;
    }
}
