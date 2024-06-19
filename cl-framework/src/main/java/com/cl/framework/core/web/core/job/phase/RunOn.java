package com.cl.framework.core.web.core.job.phase;

import com.cl.framework.core.annotation.BodyParam;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.AsyncUtils;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.invoker.InvokerUtil;
import com.cl.framework.core.web.core.worker.Mission;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.MultiMap;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.ext.auth.User;
import io.vertx.rxjava3.ext.web.Session;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class RunOn {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunOn.class);
    private transient final Refer underway = new Refer();

    RunOn() {
    }

    RunOn bind(final Refer underway) {
        if (Objects.nonNull(underway)) {
            this.underway.add(underway.get());
        }
        return this;
    }

    Single<Envelop> invoke(final Envelop envelop, final Mission mission) {
        final Method method = mission.getOn();
        if (Objects.nonNull(method)) {
            Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_3RD_JOB_RUN, mission.getCode(), method.getName())));
            return this.execute(envelop, method, mission);
        } else {
            return Single.just(envelop);
        }
    }

    Single<Envelop> callback(final Envelop envelop, final Mission mission) {
        final Method method = mission.getOff();
        if (Objects.nonNull(method)) {
            Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_6TH_JOB_CALLBACK, mission.getCode(), method.getName())));
            return this.execute(envelop, method, mission);
        } else {
            return Single.just(envelop);
        }

    }

    private Single<Envelop> execute(final Envelop envelop, final Method method, final Mission mission) {
        if (envelop.valid()) {
            final Object proxy = mission.getProxy();
            try {
                final Object[] arguments = this.buildArgs(envelop, method, mission);
                return InvokerUtil.invokeAsync(proxy, method, arguments).toSingle()
                        .flatMap(this::normalize);
            } catch (final Throwable ex) {
                ex.printStackTrace();
                return Single.error(ex);
            }
        } else {
            Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_ERROR, mission.getCode(), envelop.getError().getClass().getName())));
            return AsyncUtils.single(envelop);
        }
    }

    private <T> Single<Envelop> normalize(final T returnValue) {
        if (Objects.isNull(returnValue)) {
            return Single.just(Envelop.okJson());
        } else {
            if (Envelop.class == returnValue.getClass()) {
                return Single.just((Envelop) returnValue);
            } else {
                return Single.just(Envelop.success(returnValue));
            }
        }
    }

    private Object[] buildArgs(final Envelop envelop, final Method method, final Mission mission) {
        final Class<?>[] parameters = method.getParameterTypes();
        final List<Object> argsList = new ArrayList<>();
        if (0 < parameters.length) {
            for (final Class<?> parameterType : parameters) {
                argsList.add(this.buildArgs(parameterType, envelop, mission));
            }
        } else {
            throw new DataTableException(ErrorCodeEnum.PARAM_ERROR_CODE, MessageFormat.format(ErrorInfoConstant.JOB_METHOD_ERROR, this.getClass(), mission.getCode()));
        }
        return argsList.toArray();
    }

    private Object buildArgs(final Class<?> clazz, final Envelop envelop, final Mission mission) {
        if (Envelop.class == clazz) {
            return envelop;
        } else if (ReflectionUtils.isMatch(clazz, Session.class)) {
            return envelop.getAssist().getSession();
        } else if (ReflectionUtils.isMatch(clazz, User.class)) {
            return envelop.getAssist().getUser();
        } else if (MultiMap.class == clazz) {
            return envelop.getAssist().getHeaders();
        } else  if (JsonObject.class == clazz) {
            if (clazz.isAnnotationPresent(BodyParam.class)) {
                return envelop.getData();
            } else {
                return mission.getAdditional().copy();
            }
        } else if (Mission.class == clazz) {
            return mission;
        } else if (Refer.class == clazz) {
            return this.underway;
        } else {
            throw new DataTableException(ErrorCodeEnum.PARAM_ERROR_CODE, MessageFormat.format(ErrorInfoConstant.JOB_METHOD_ERROR, this.getClass(), mission.getCode()));
        }
    }
}
