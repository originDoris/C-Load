package com.cl.framework.core.web.core.route.parse;

import com.cl.framework.core.constants.ParamIdConstant;
import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.DataTableSerializer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.param.ParamContainer;
import com.cl.framework.core.web.core.param.filler.Filler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.ext.web.RoutingContext;

import javax.ws.rs.DefaultValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提取ParamContainer
 * @author xhz
 */
public class ParamContainerIncome implements Income<List<ParamContainer<Object>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamContainerIncome.class);

    private transient final Atomic<Object> atomic = ReflectionUtils.singleton(MimeAtomic.class);

    @Override
    public List<ParamContainer<Object>> in(final RoutingContext context,
                                    final Event event)
            throws WebException {
        final Method method = event.getAction();
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Annotation[][] annoTypes = method.getParameterAnnotations();
        final List<ParamContainer<Object>> args = new ArrayList<>();
        for (int idx = 0; idx < paramTypes.length; idx++) {

            final ParamContainer<Object> epsilon = new ParamContainer<>();
            epsilon.setArgType(paramTypes[idx]);
            epsilon.setAnnotation(this.getAnnotation(annoTypes[idx]));
            epsilon.setName(this.getName(epsilon.getAnnotation()));


            epsilon.setDefaultValue(this.getDefault(annoTypes[idx], epsilon.getArgType()));

            final ParamContainer<Object> outcome = this.atomic.ingest(context, epsilon);
            args.add(CubeFn.getDefault(null, () -> outcome, outcome));
        }
        return args;
    }

    @SuppressWarnings("all")
    private String getName(final Annotation annotation) {
        return CubeFn.getSemi(null == annotation, LOGGER,
                () -> ParamIdConstant.IGNORE,
                () -> CubeFn.getSemi(!Filler.NO_VALUE.contains(annotation.annotationType()),
                        LOGGER,
                        () -> ReflectionUtils.invoke(annotation, "value"),
                        () -> ParamIdConstant.DIRECT));
    }

    private Annotation getAnnotation(final Annotation[] annotations) {
        final List<Annotation> annotationList = Arrays.stream(annotations)
                .filter(item -> Filler.PARAMS.containsKey(item.annotationType()))
                .collect(Collectors.toList());
        return annotationList.isEmpty() ? null : annotationList.get(0);
    }

    private Object getDefault(final Annotation[] annotations,
                              final Class<?> paramType) {
        final List<Annotation> annotationList = Arrays.stream(annotations)
                .filter(item -> item.annotationType() == DefaultValue.class)
                .collect(Collectors.toList());
        return CubeFn.getSemi(annotationList.isEmpty(), LOGGER,
                () -> null,
                () -> {
                    final Annotation annotation = annotationList.get(0);
                    return DataTableSerializer.getValue(paramType, ReflectionUtils.invoke(annotation, "value"));
                });
    }
}
