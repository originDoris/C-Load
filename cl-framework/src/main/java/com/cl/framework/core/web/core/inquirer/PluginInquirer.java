package com.cl.framework.core.web.core.inquirer;


import com.cl.framework.core.annotation.Plugin;
import io.reactivex.rxjava3.core.Observable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * 扫描Plugin注解
 * @author xhz
 */
public class PluginInquirer implements Inquirer<Set<Class<?>>> {

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> allClasses) {
        final Set<Class<?>> plugins = new HashSet<>();
        Observable.fromIterable(allClasses)
                .filter(this::isPlugin)
                .subscribe(plugins::add)
                .dispose();
        return plugins;
    }

    private boolean isPlugin(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        final Long counter = Observable.fromArray(fields)
                .filter(field -> field.isAnnotationPresent(Plugin.class))
                .count().blockingGet();
        return 0 < counter;
    }
}
