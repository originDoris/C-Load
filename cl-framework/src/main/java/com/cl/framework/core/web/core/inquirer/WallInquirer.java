package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.annotation.Wall;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.options.transformer.CliffTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.options.visitor.CliffVisitor;
import com.cl.framework.core.options.visitor.ConfigVisitor;
import com.cl.framework.core.utils.CodecUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.secure.Cliff;
import com.cl.framework.core.web.core.secure.OstiumAuth;
import com.cl.framework.core.web.core.secure.PhylumAuth;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;


import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 负责扫描wall注解
 * @author xhz
 */
public class WallInquirer implements Inquirer<Set<Cliff>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WallInquirer.class);

    private static final String KEY = "secure";

    private transient final Transformer<Cliff> cliffTransformer = ReflectionUtils.singleton(CliffTransformer.class);
    private transient final ConfigVisitor<Cliff> cliffConfigVisitor = ReflectionUtils.singleton(CliffVisitor.class);


    @Override
    public Set<Cliff> scan(final Set<Class<?>> walls) {
        final Set<Cliff> wallSet = new TreeSet<>();
        final Set<Class<?>> wallClses = walls.stream()
                .filter((item) -> item.isAnnotationPresent(Wall.class))
                .collect(Collectors.toSet());
        if (!wallClses.isEmpty()) {
            final ConcurrentMap<String, Class<?>> keys = new ConcurrentHashMap<>();
            final JsonObject config = verify(wallClses, keys);
            for (final String field : config.fieldNames()) {
                final Class<?> cls = keys.get(field);
                final Cliff cliff = cliffTransformer.transform(config.getJsonObject(field));
                mountData(cliff, cls);
                wallSet.add(cliff);
            }
        }
        return wallSet;
    }

    private void mountData(final Cliff cliff, final Class<?> clazz) {
        mountAnno(cliff, clazz);
        if (cliff.isDefined()) {
            OstiumAuth.create(clazz).verify().mount(cliff);
        } else {
            PhylumAuth.create(clazz).verify().mount(cliff);
        }
    }

    private void mountAnno(final Cliff cliff, final Class<?> clazz) {
        final Annotation annotation = clazz.getAnnotation(Wall.class);
        cliff.setOrder(ReflectionUtils.invoke(annotation, "order"));
        cliff.setPath(ReflectionUtils.invoke(annotation, "path"));
        cliff.setDefined(ReflectionUtils.invoke(annotation, "define"));
    }



    private JsonObject verify(final Set<Class<?>> wallClses,
                              final ConcurrentMap<String, Class<?>> keysRef) {

        final Set<String> hashs = new HashSet<>();
        Observable.fromIterable(wallClses)
                .filter(Objects::nonNull)
                .map(item -> {
                    final Annotation annotation = item.getAnnotation(Wall.class);
                    keysRef.put(ReflectionUtils.invoke(annotation, "value"), item);
                    return hashPath(annotation);
                }).subscribe(hashs::add).dispose();

        CubeFn.outError(LOGGER, hashs.size() != wallClses.size(),
                DataTableException.class, ErrorCodeEnum.WALL_DUPLICATED_ERROR,
                MessageFormat.format(ErrorInfoConstant.WALL_DUPLICATED_ERROR, wallClses.stream().map(Class::getName).collect(Collectors.toSet())));

        final JsonObject config = cliffConfigVisitor.getConfig(KEY);

        for (final String key : keysRef.keySet()) {
            CubeFn.outError(LOGGER,null == config || !config.containsKey(key),
                    DataTableException.class, ErrorCodeEnum.WALL_KEY_MISSING_ERROR,
                    MessageFormat.format(ErrorInfoConstant.WALL_KEY_MISSING_ERROR, key, keysRef.get(key)));

        }
        return config;
    }

    /**
     * Path或Order不能相同或重复
     */
    private String hashPath(final Annotation annotation) {
        final Integer order = ReflectionUtils.invoke(annotation, "order");
        final String path = ReflectionUtils.invoke(annotation, "path");
        return CodecUtil.sha512(order + path);
    }
}
