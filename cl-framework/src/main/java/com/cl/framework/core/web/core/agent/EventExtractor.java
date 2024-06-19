package com.cl.framework.core.web.core.agent;

import com.cl.framework.core.annotation.Adjust;
import com.cl.framework.core.annotation.Anno;
import com.cl.framework.core.annotation.EndPoint;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.exception.EventSourceException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.param.resolver.MediaResolver;
import com.cl.framework.core.web.core.param.resolver.MethodResolver;
import com.cl.framework.core.web.core.param.resolver.PathResolver;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.vertx.VertxApplication;
import com.cl.framework.core.web.core.Virtual;
import com.cl.framework.core.web.core.Extractor;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 扫描 @EndPoint 类构建Event元数据
 *
 * @author xhz
 */
@Slf4j
public class EventExtractor implements Extractor<Set<Event>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventExtractor.class);

    @Override
    public Set<Event> extract(final Class<?> clazz) {
        verify(clazz);
        final Set<Event> result = new ConcurrentHashSet<>();
        CubeFn.safeSemi(clazz.isAnnotationPresent(Path.class),
                () -> {
                    final Path path = Anno.getPath(clazz);
                    assert null != path : "Path should not be null.";
                    result.addAll(extract(clazz, PathResolver.resolve(path)));
                },
                () -> {
                    result.addAll(extract(clazz, null));
                }, LOGGER);
        return result;
    }

    private void verify(final Class<?> clazz) {
        CubeFn.safeSemi(!clazz.isInterface(), () -> {
            CubeFn.outError(LOGGER, !ReflectionUtils.noarg(clazz), DataTableException.class, ErrorCodeEnum.NO_ARG_CONSTRUCTOR, MessageFormat.format(ErrorInfoConstant.NO_ARG_CONSTRUCTOR_ERROR, clazz.getName()));
        },LOGGER);
        CubeFn.outError(LOGGER, !Modifier.isPublic(clazz.getModifiers()), DataTableException.class, ErrorCodeEnum.NO_ARG_CONSTRUCTOR, MessageFormat.format(ErrorInfoConstant.NOT_PUBLIC_ERROR, clazz.getName()));
        CubeFn.outError(LOGGER, !clazz.isAnnotationPresent(EndPoint.class), EventSourceException.class, ErrorCodeEnum.NO_ARG_CONSTRUCTOR, MessageFormat.format(ErrorInfoConstant.NOT_PUBLIC_ERROR, clazz.getName()));
    }

    @SuppressWarnings("all")
    private Set<Event> extract(final Class<?> clazz, final String root) {
        final Set<Event> events = new ConcurrentHashSet<>();
        final Method[] methods = clazz.getDeclaredMethods();
        events.addAll(Arrays.stream(methods).filter(MethodResolver::isValid)
                .map(item -> this.extract(item, root))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        return events;
    }

    private Event extract(final Method method, final String root) {
        final Event event = new Event();
        final HttpMethod httpMethod = MethodResolver.resolve(method);
        if (null == httpMethod) {
            return null;
        } else {
            event.setMethod(httpMethod);
        }

        final Path path = Anno.getPath(method);
        if (null == path) {
            if (StringUtils.isNotBlank(root)) {
                event.setPath(root);
            }
        } else {
            final String result = PathResolver.resolve(path, root);
            event.setPath(result);
        }

        event.setAction(method);
        event.setConsumes(MediaResolver.consumes(method));
        event.setProduces(MediaResolver.produces(method));
        // 如果是interface 则寻找对应的实现类做代理
        final Class<?> clazz = method.getDeclaringClass();
        final Object proxy;
        if (clazz.isInterface()) {
            Class<?> uniqueChild = ReflectionUtils.getUniqueChild(com.cl.framework.core.runtime.DataTablePack.getClasses(VertxApplication.scanBasePackages), clazz);
            if (null != uniqueChild) {
                proxy = ReflectionUtils.singleton(uniqueChild);
            } else {
                // 只有interface 没有实现类时 设置一个虚拟类 什么也不做
                proxy = Virtual.create();
            }
        } else {
            proxy = ReflectionUtils.singleton(method.getDeclaringClass());
        }
        event.setProxy(proxy);

        if (method.isAnnotationPresent(Adjust.class)) {
            final Annotation adjust = method.getDeclaredAnnotation(Adjust.class);
            final Integer order = ReflectionUtils.invoke(adjust, "value");
            if (Objects.nonNull(order)) {
                event.setOrder(order);
            }
        }
        return event;
    }
}
