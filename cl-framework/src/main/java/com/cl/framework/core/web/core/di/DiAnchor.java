package com.cl.framework.core.web.core.di;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.plugin.Infix;
import com.cl.framework.plugin.Plugins;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Set;

class DiAnchor {

    private transient final Class<?> clazz;
    private transient final Logger logger;

    DiAnchor(final Class<?> clazz) {
        this.clazz = clazz;
        this.logger = LoggerFactory.getLogger(clazz);
    }

    private Class<? extends Annotation> search(final Field field) {
        final Annotation[] annotations = field.getDeclaredAnnotations();
        final Set<Class<? extends Annotation>> annotationCls = Plugins.INFIX_MAP.keySet();
        Class<? extends Annotation> hitted = null;
        for (final Annotation annotation : annotations) {
            if (annotationCls.contains(annotation.annotationType())) {
                hitted = annotation.annotationType();
                break;
            }
        }
        return hitted;
    }

    Object initialize(final Field field) {
        final Class<? extends Annotation> key = this.search(field);
        final String pluginKey = Plugins.INFIX_MAP.get(key);
        final Class<?> infixCls = com.cl.framework.core.runtime.DataTableAmbient.getPlugin(pluginKey);
        Object ret = null;
        if (null != infixCls) {
            if (ReflectionUtils.isMatch(infixCls, Infix.class)) {

                final Infix reference = (Infix) ReflectionUtils.singleton(infixCls);

                ret = ReflectionUtils.invoke(reference, "get");
            } else {
                this.logger.warn(MessageFormat.format(MessageConstant.INFIX_IMPL, infixCls.getName(), Infix.class.getName()));
            }
        } else {
            this.logger.warn(MessageFormat.format(MessageConstant.INFIX_NULL, pluginKey, field.getName(), field.getDeclaringClass().getName()));
        }
        return ret;
    }
}
