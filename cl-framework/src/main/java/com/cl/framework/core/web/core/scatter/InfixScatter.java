package com.cl.framework.core.web.core.scatter;

import com.cl.framework.core.annotation.Plugin;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.DataTableAmbient;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.runtime.Runner;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.di.DiPlugin;
import com.cl.framework.plugin.Plugins;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.Vertx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Infix initialization
 * @author xhz
 */
public class InfixScatter implements Scatter<Vertx> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfixScatter.class);

    private static final Set<Class<?>> PLUGINS = DataTableAnno.getTps();

    private static final DiPlugin PLUGIN = DiPlugin.create(InfixScatter.class);

    @Override
    @SuppressWarnings("all")
    public void connect(final Vertx vertx) {
        final ConcurrentMap<String, Class<?>> wholeInjections = DataTableAmbient.getInjections();

        final ConcurrentMap<Class<? extends Annotation>, Class<?>> injections = CubeFn.reduce(Plugins.INFIX_MAP, wholeInjections);
        // 找到插件并执行其init方法，这里主要处理框架的插件
        injections.values().forEach(item -> {
            if (null != item && item.isAnnotationPresent(Plugin.class)) {
                final Method method = findInit(item);
                CubeFn.outError(LOGGER,null == method,
                        DataTableException.class, ErrorCodeEnum.PLUGIN_SPECIFICATION_ERROR,
                        MessageFormat.format(ErrorInfoConstant.PLUGIN_SPECIFICATION_ERROR, getClass(), item.getName()));
                CubeFn.safeJvm(() -> method.invoke(null, vertx), LOGGER);
            }
        });

        // 处理用户自定义插件
        Observable.fromIterable(wholeInjections.keySet())
                .filter(key -> !Plugins.Infix.STANDAND.contains(key))
                .map(wholeInjections::get)
                .filter(Objects::nonNull)
                .filter(item -> item.isAnnotationPresent(Plugin.class))
                .subscribe(item -> {
                    final Method method = findInit(item);
                    CubeFn.outError( LOGGER,null == method,
                            DataTableException.class, ErrorCodeEnum.PLUGIN_SPECIFICATION_ERROR,
                            MessageFormat.format(ErrorInfoConstant.PLUGIN_SPECIFICATION_ERROR, getClass(), item.getName()));
                    CubeFn.safeJvm(() -> method.invoke(null, vertx), LOGGER);
                })
                .dispose();

        // 对所有使用Plugin注解的属性赋值
        CubeFn.itSet(PLUGINS, (clazz, index) -> Runner.run(() -> {
            final Object reference = ReflectionUtils.singleton(clazz);
            PLUGIN.inject(reference);
        }, "injects-plugin-scannner"));
    }


    private Method findInit(final Class<?> clazz) {
        return CubeFn.getDefault(null,() -> {
            final Method[] methods = clazz.getDeclaredMethods();
            final List<Method> found = Arrays.stream(methods)
                    .filter(item -> "init".equals(item.getName()) && this.validMethod(item))
                    .collect(Collectors.toList());
            return 1 == found.size() ? found.get(0) : null;
        }, clazz);
    }

    private boolean validMethod(final Method method) {
        return (void.class == method.getReturnType() || Void.class == method.getReturnType())
                && Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers());
    }
}
