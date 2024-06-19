package com.cl.framework.core.web.core.param.resolver;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.EventExtractor;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * MediaResolver
 * consumes ( default = application/json )
 * produces ( default = application/json )
 * @author xhz
 */
public class MediaResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventExtractor.class);

    private static final Set<MediaType> DEFAULTS = new HashSet<MediaType>() {
        {
            add(MediaType.WILDCARD_TYPE);
        }
    };

    /**
     * Capture the consume mime types
     *
     * @param method method reference
     * @return return MIME
     */
    public static Set<MediaType> consumes(final Method method) {
        return resolve(method, Consumes.class);
    }

    /**
     * Capture the produces mime types
     *
     * @param method method reference
     * @return return MIME
     */
    public static Set<MediaType> produces(final Method method) {
        return resolve(method, Produces.class);
    }

    private static Set<MediaType> resolve(final Method method, final Class<? extends Annotation> mediaCls) {
        return CubeFn.getDefault(null, () -> {
            final Annotation anno = method.getAnnotation(mediaCls);
            return CubeFn.getSemi(null == anno, LOGGER,
                    () -> DEFAULTS,
                    () -> {
                        String[] value = ReflectionUtils.invoke(anno, "value");
                        final Set<MediaType> result = new HashSet<>();
                        Observable.fromArray(value)
                                .filter(Objects::nonNull)
                                .map(MediaType::valueOf)
                                .filter(Objects::nonNull)
                                .subscribe(result::add)
                                .dispose();
                        return result.isEmpty() ? DEFAULTS : result;
                    });
        }, method, mediaCls);
    }
}
