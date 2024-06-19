package com.cl.framework.core.web.core.secure;

import com.cl.framework.core.annotation.Authenticate;
import com.cl.framework.core.annotation.Authorize;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author xhz
 */
public class PhylumAuth {

    private final transient Class<?> clazz;
    private final transient Logger logger;
    private final transient Method[] methods;

    private PhylumAuth(final Class<?> clazz) {
        this.clazz = clazz;
        this.logger = LoggerFactory.getLogger(clazz);
        this.methods = clazz.getDeclaredMethods();
    }

    public static PhylumAuth create(final Class<?> clazz) {
        return new PhylumAuth(clazz);
    }

    public PhylumAuth verify() {
        CubeFn.outError(this.logger, this.verifyMethod(this.methods, Authenticate.class),
                DataTableException.class, ErrorCodeEnum.WALL_METHOD_MULTI_ERROR,
                MessageFormat.format(ErrorInfoConstant.WALL_METHOD_MULTI_ERROR, Authenticate.class.getSimpleName(), this.clazz.getName()));

        CubeFn.outError(this.logger, this.verifyMethod(this.methods, Authorize.class),
                DataTableException.class, ErrorCodeEnum.WALL_METHOD_MULTI_ERROR,
                MessageFormat.format(ErrorInfoConstant.WALL_METHOD_MULTI_ERROR, Authorize.class.getSimpleName(), this.clazz.getName()));
        return this;
    }

    public void mount(final Cliff reference) {
        reference.setProxy(ReflectionUtils.singleton(this.clazz));
        final Optional<Method> authenticateMethod = Arrays.stream(this.methods)
                .filter(item -> item.isAnnotationPresent(Authenticate.class))
                .findFirst();
        reference.getAuthorizer().setAuthenticate(authenticateMethod.orElse(null));

        final Optional<Method> authorizeMethod
                = Arrays.stream(this.methods).filter(
                item -> item.isAnnotationPresent(Authorize.class))
                .findFirst();
        reference.getAuthorizer().setAuthorize(authorizeMethod.orElse(null));
    }

    private boolean verifyMethod(final Method[] methods,
                                 final Class<? extends Annotation> clazz) {

        final long found = Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(clazz))
                .count();
        return 1 < found;
    }
}
