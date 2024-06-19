package com.cl.framework.core.web.core.thread;

import com.cl.framework.core.annotation.Qualifier;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.StringUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.plugin.Plugins;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class AffluxThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(AffluxThread.class);

    private final ConcurrentMap<String, Class<?>> fieldMap = new ConcurrentHashMap<>();

    private final transient Class<?> reference;
    private final transient Set<Class<?>> namedSet;
    private final transient Set<Class<?>> classes;

    public AffluxThread(final Class<?> clazz, final Set<Class<?>> classes) {
        this.setName("cl.injection-scanner-" + this.getId());
        this.reference = clazz;
        this.classes = classes;
        this.namedSet = classes.stream()
                .filter((item) -> item.isAnnotationPresent(Named.class))
                .collect(Collectors.toSet());
    }

    @Override
    public void run() {
        if (null != this.reference) {

            // 读取所有的注入点
            final List<Field> fields = ReflectionUtils.getAllDeclaredFields(this.reference).stream()
                    .filter(field -> Plugins.INJECT_ANNOTATIONS.stream().anyMatch(field::isAnnotationPresent))
                    .collect(Collectors.toList());
            for (final Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    this.scanStandard(field);
                } else {
                    this.scanSpecific(field);
                }
            }
        }
    }

    private void scanStandard(final Field field) {
        // JSR 330
        final Class<?> type = field.getType();
        if (type.isInterface()) {
            final List<Class<?>> target = this.classes.stream().filter(item -> ReflectionUtils.isMatch(item, type)).collect(Collectors.toList());
            if (target.isEmpty()) {
                // 0 size of current size here
                final String fieldName = field.getName();
                final String typeName = field.getDeclaringClass().getName();
                LOGGER.warn(MessageFormat.format(MessageConstant.SCANNED_JSR311, typeName, fieldName, type.getName()));
            } else {
                // Unique
                if (1 == target.size()) {
                    final Class<?> targetCls = target.get(0);
                    LOGGER.info(MessageFormat.format(MessageConstant.SCANNED_FIELD, this.reference, field.getName(), targetCls.getName(), Inject.class));
                    this.fieldMap.put(field.getName(), targetCls);
                } else {
                    // By Named and Qualifier
                    this.scanQualifier(field, target);
                }
            }
        } else {
            this.fieldMap.put(field.getName(), type);
            LOGGER.info(MessageFormat.format(MessageConstant.SCANNED_FIELD, this.reference, field.getName(), type.getName(), Inject.class));
        }

    }

    private void scanQualifier(final Field field,
                               final List<Class<?>> instanceCls) {
        final List<String> instanceNames = instanceCls.stream().map(Class::getName).collect(Collectors.toList());
        LOGGER.info(MessageFormat.format(MessageConstant.SCANNED_INSTANCES, StringUtil.join(instanceNames, null)));

        final Annotation annotation = field.getAnnotation(Qualifier.class);

        CubeFn.outError(LOGGER, null == annotation,
                DataTableException.class, ErrorCodeEnum.QUALIFIER_MISSED_ERROR,
                MessageFormat.format(ErrorInfoConstant.QUALIFIER_MISSED_ERROR, this.getClass(), field.getName(), field.getDeclaringClass().getName()));

        final boolean match = instanceCls.stream()
                .allMatch(item -> item.isAnnotationPresent(Named.class));

        final Set<String> names = instanceCls.stream()
                .map(Class::getName).collect(Collectors.toSet());

        CubeFn.outError(LOGGER, !match,
                DataTableException.class, ErrorCodeEnum.NAMED_IMPL_ERROR,
                MessageFormat.format(ErrorInfoConstant.NAMED_IMPL_ERROR, this.getClass(), names, field.getType().getName())
        );

        final String value = ReflectionUtils.invoke(annotation, "value");

        final Optional<Class<?>> verified = instanceCls.stream()
                .filter(item -> {
                    final Annotation target = item.getAnnotation(Named.class);
                    final String targetValue = ReflectionUtils.invoke(target, "value");
                    return value.equals(targetValue)
                            && StringUtils.isNotBlank(targetValue);
                }).findAny();

        CubeFn.outError(LOGGER, !verified.isPresent(),
                DataTableException.class, ErrorCodeEnum.NAME_NOT_FOUND_ERROR,
                MessageFormat.format(ErrorInfoConstant.NAMED_NOT_FOUND, this.getClass(), names, value));

        this.fieldMap.put(field.getName(), verified.get());
    }

    private void scanSpecific(final Field field) {
        final Set<Class<? extends Annotation>> defineds = Plugins.INFIX_MAP.keySet();
        final Annotation[] annotations = field.getDeclaredAnnotations();

        final Set<String> set = new HashSet<>();
        final Annotation hitted = Observable.fromArray(annotations)
                .filter(annotation -> defineds.contains(annotation.annotationType()))
                .map(annotation -> {
                    set.add(annotation.annotationType().getName());
                    return annotation;
                }).blockingFirst();

        CubeFn.outError(LOGGER, 1 < set.size(),
                DataTableException.class, ErrorCodeEnum.ANNOTATION_REPEAT_ERROR,
                MessageFormat.format(ErrorInfoConstant.ANNOTATION_REPEAT_ERROR, this.getClass(),
                        field.getName(), field.getDeclaringClass().getName(), set));

        LOGGER.info(MessageFormat.format(MessageConstant.SCANNED_FIELD, this.reference,
                field.getName(),
                field.getDeclaringClass().getName(),
                hitted.annotationType().getName()));
        this.fieldMap.put(field.getName(), field.getType());
    }

    public ConcurrentMap<String, Class<?>> getFieldMap() {
        return this.fieldMap;
    }

    public Class<?> getClassKey() {
        return this.reference;
    }

    public boolean isEmpty() {
        return this.fieldMap.isEmpty();
    }
}
