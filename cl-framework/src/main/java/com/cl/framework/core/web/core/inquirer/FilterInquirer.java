package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.annotation.FilterOrder;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.Orders;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.filter.Filter;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.servlet.annotation.WebFilter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 扫描WebFilter 注解
 *
 * @author xhz
 */
public class FilterInquirer implements Inquirer<ConcurrentMap<String, Set<Event>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterInquirer.class);


    @Override
    public ConcurrentMap<String, Set<Event>> scan(Set<Class<?>> classes) {
        final ConcurrentMap<String, Set<Event>> filters = new ConcurrentHashMap<>();
        Observable.fromIterable(classes)
                .filter(item -> item.isAnnotationPresent(WebFilter.class))
                .map(this::ensure)
                .subscribe(item -> this.extract(filters, item))
                .dispose();
        return filters;
    }


    private Class<?> ensure(final Class<?> clazz) {
        CubeFn.outError(LOGGER, !Filter.class.isAssignableFrom(clazz), DataTableException.class,
                ErrorCodeEnum.FILTER_SPECIFICATION_ERROR, MessageFormat.format(ErrorInfoConstant.FILTER_SPECIFICATION_ERROR, clazz.getName(), Filter.class.getName()));
        return clazz;
    }

    private void extract(final ConcurrentMap<String, Set<Event>> map,
                         final Class<?> clazz) {
        final Annotation annotation = clazz.getAnnotation(WebFilter.class);
        final String[] pathes = ReflectionUtils.invoke(annotation, "value");
        for (final String path : pathes) {
            final Event event = this.extract(path, clazz);
            Set<Event> events = map.get(path);
            if (null == events) {
                events = new HashSet<>();
            }
            events.add(event);
            map.put(path, events);
        }
    }

    private Event extract(final String path, final Class<?> clazz) {
        final Event event = new Event();
        event.setPath(path);
        final Annotation annotation = clazz.getAnnotation(FilterOrder.class);
        int order = Orders.FILTER;
        if (null != annotation) {
            final Integer setted = ReflectionUtils.invoke(annotation, "value");
            CubeFn.outError(LOGGER, setted < 0,
                    DataTableException.class, ErrorCodeEnum.FILTER_ORDER_ERROR, MessageFormat.format(ErrorInfoConstant.FILTER_ORDER_ERROR, clazz.getName()));
            order = order + setted;
        }
        event.setOrder(order);
        final Object proxy = ReflectionUtils.singleton(clazz);
        CubeFn.outError(LOGGER, null == proxy,
                DataTableException.class, ErrorCodeEnum.FILTER_INIT_ERROR, MessageFormat.format(ErrorInfoConstant.FILTER_INIT_ERROR, clazz.getName()));
        event.setProxy(proxy);
        final Method action = this.findMethod(clazz);
        event.setAction(action);
        event.setConsumes(new HashSet<>());
        event.setProduces(new HashSet<>());
        return event;
    }

    private Method findMethod(final Class<?> clazz) {
        final List<Method> methods = new ArrayList<>();
        final Method[] scanned = clazz.getDeclaredMethods();
        Observable.fromArray(scanned)
                .filter(item -> "doFilter".equals(item.getName()))
                .subscribe(methods::add)
                .dispose();
        if (1 == methods.size()) {
            return methods.get(0);
        } else {
            return Observable.fromIterable(methods)
                    .filter(this::isValidFilter)
                    .blockingFirst();
        }
    }

    private boolean isValidFilter(final Method method) {
        final Class<?>[] parameters = method.getParameterTypes();
        boolean valid = false;
        if (2 == parameters.length) {
            final Class<?> requestCls = parameters[0];
            final Class<?> responseCls = parameters[1];
            if (HttpServerRequest.class == requestCls && HttpServerResponse.class == responseCls) {
                valid = true;
            }
        }
        return valid;
    }
}
