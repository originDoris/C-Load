package com.cl.framework.core.utils;

import com.cl.framework.core.annotation.BodyParam;
import com.cl.framework.core.annotation.StreamParam;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.param.filler.Filler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 验证event 参数是否正确配置了
 * @author xhz
 */
public class VerifierEventUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifierEventUtil.class);

    @SuppressWarnings("all")
    public static void verify(final Event event) {
        final Method method = event.getAction();
        CubeFn.outError(LOGGER, null == method, com.cl.framework.core.exception.DataTableException.class,
                ErrorCodeEnum.EVENT_ACTION_NON_ERROR, MessageFormat.format(ErrorInfoConstant.EVENT_ACTION_NON_ERROR, event.toString()));
        /* Specification **/
        verify(method, BodyParam.class);
        verify(method, StreamParam.class);
        /* Field Specification **/
        for (final Parameter parameter : method.getParameters()) {
            verify(parameter);
        }
    }

    public static void verify(final Method method, final Class<? extends Annotation> annoCls) {
        final Annotation[][] annotations = method.getParameterAnnotations();
        final AtomicInteger integer = new AtomicInteger(0);
        FieldUtil.itMatrix(annotations, (annotation) -> {
            if (annotation.annotationType() == annoCls) {
                integer.incrementAndGet();
            }
        });
        final int occurs = integer.get();

        CubeFn.outError(LOGGER, 1 < occurs, com.cl.framework.core.exception.DataTableException.class,
                ErrorCodeEnum.ANNOTATION_REPEAT_ERROR,
                MessageFormat.format(ErrorInfoConstant.ANNOTATION_REPEAT_ERROR, annoCls, method.getName(), occurs)
        );
    }

    public static void verify(final Parameter parameter) {
        final Annotation[] annotations = parameter.getDeclaredAnnotations();
        final List<Annotation> annotationList = Arrays.stream(annotations)
                .filter(item -> Filler.PARAMS.containsKey(item.annotationType()))
                .collect(Collectors.toList());

        final int multi = annotationList.size();
        CubeFn.outError(LOGGER,1 < multi , com.cl.framework.core.exception.DataTableException.class,
                VerifierEventUtil.class, parameter.getName(), multi);
    }
}
