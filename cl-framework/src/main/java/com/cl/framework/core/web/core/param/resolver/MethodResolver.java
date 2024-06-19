package com.cl.framework.core.web.core.param.resolver;

import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.MethodNullException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.agent.EventExtractor;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * MethodResolver
 *
 * @author xhz
 */
public class MethodResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventExtractor.class);

    private static final ConcurrentMap<Class<?>, HttpMethod> METHODS =
            new ConcurrentHashMap<Class<?>, HttpMethod>() {
                {
                    this.put(GET.class, HttpMethod.GET);
                    this.put(POST.class, HttpMethod.POST);
                    this.put(PUT.class, HttpMethod.PUT);
                    this.put(DELETE.class, HttpMethod.DELETE);
                    this.put(OPTIONS.class, HttpMethod.OPTIONS);
                    this.put(HEAD.class, HttpMethod.HEAD);
                    this.put(PATCH.class, HttpMethod.PATCH);
                }
            };

    @SuppressWarnings("all")
    public static HttpMethod resolve(final Method method) {
        CubeFn.outError(LOGGER, null == method, MethodNullException.class, ErrorCodeEnum.METHOD_IS_NULL);
        final Annotation[] annotations = method.getDeclaredAnnotations();
        HttpMethod result = null;
        for (final Annotation annotation : annotations) {
            final Class<?> key = annotation.annotationType();
            if (METHODS.containsKey(key)) {
                result = METHODS.get(key);
                break;
            }
        }
        return result;
    }

    public static boolean isValid(final Method method) {
        final int modifiers = method.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers);
    }


}
