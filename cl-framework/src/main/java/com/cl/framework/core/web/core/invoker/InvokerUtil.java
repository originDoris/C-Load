package com.cl.framework.core.web.core.invoker;


import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.DataTableSerializer;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.param.serialization.TypedArgument;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.Session;
import org.jooq.impl.QOM;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * invoker 工具类
 * @author xhz
 */
@SuppressWarnings("unused")
public class InvokerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvokerUtil.class);

    static boolean isVoid(final Method method) {
        final Class<?> returnType = method.getReturnType();
        return void.class == returnType || Void.class == returnType;
    }

    public static void verifyArgs(final Method method, final Class<?> target) {

        final Class<?>[] params = method.getParameterTypes();
        Logger logger = LoggerFactory.getLogger(target);
        CubeFn.outError(logger, 0 == params.length,
                DataTableException.class, ErrorCodeEnum.WORKER_ARGUMENT_ERROR,
                MessageFormat.format(ErrorInfoConstant.WORKER_ARGUMENT_ERROR,target, method));
    }

    public static void verify(final boolean condition, final Class<?> returnType, final Class<?> paramType, final Class<?> target) {
        Logger logger = LoggerFactory.getLogger(target);
        CubeFn.outError(logger, condition,
                DataTableException.class,
                ErrorCodeEnum.ASYNC_SIGNATURE_ERROR,
                MessageFormat.format(ErrorInfoConstant.ASYNC_SIGNATURE_ERROR, target, returnType.getName(), paramType.getName()));
    }

    private static Object getValue(final Class<?> type, final Envelop envelop, final Supplier<Object> defaultSupplier) {
        final Object value;
        if (Session.class == type) {
            value = envelop.getAssist().getSession();
        } else {
            value = defaultSupplier.get();
            final Object argument = null == value ? null : DataTableSerializer.getValue(type, value.toString());
        }
        return value;
    }

    static Object invokeMulti(final Object proxy, final Method method, final Envelop envelop) {
        final Object reference = envelop.getData();
        final Object[] arguments = new Object[method.getParameterCount()];
        final JsonObject json = (JsonObject) reference;
        final Class<?>[] types = method.getParameterTypes();
        JsonObject paramJson = json.getJsonObject("data");
        int adjust = 0;
        for (int idx = 0; idx < types.length; idx++) {

            final Class<?> type = types[idx];
            final Object analyzed = TypedArgument.analyze(envelop, type);
            if (Objects.isNull(analyzed)) {
                final int current = idx - adjust;
                final Object value = paramJson.getValue(String.valueOf(current));
                if (Objects.isNull(value)) {
                    arguments[idx] = null;
                } else {
                    arguments[idx] = DataTableSerializer.getValue(type, value.toString());
                }
            } else {
                arguments[idx] = analyzed;
                adjust += 1;
            }
        }
        return ReflectionUtils.invoke(proxy, method.getName(), arguments);
    }

    static Object invokeSingle(final Object proxy,
                               final Method method,
                               final Envelop envelop) {
        final Class<?> argType = method.getParameterTypes()[0];
        final Object reference = Objects.nonNull(envelop) ? envelop.getData() : new JsonObject();
        Object parameters = reference;
        if (JsonObject.class == reference.getClass()) {
            final JsonObject json =  ((JsonObject) reference).getJsonObject("data");
            if (isInterface(json)) {
                if (1 == json.fieldNames().size()) {
                    parameters = json.getValue("0");
                }
            }
        }
        final Object arguments = DataTableSerializer.getValue(argType, FieldUtil.toString(parameters));
        return ReflectionUtils.invoke(proxy, method.getName(), arguments);
    }

   public static <T> Maybe<T> invokeAsync(final Object instance,
                                     final Method method,
                                     final Object... args) {
        final Class<?> returnType = method.getReturnType();
        try {
            if (void.class == returnType) {
                CubeFn.outError(LOGGER, method.getParameters().length != args.length + 1,
                        DataTableException.class, ErrorCodeEnum.INVOKING_SPEC_ERROR,
                        MessageFormat.format(ErrorInfoConstant.INVOKING_SPEC_ERROR, Invoker.class, method));
                final Promise<T> promise = Promise.promise();
                final Object[] arguments = ReflectionUtils.add(args, promise.future());
                method.invoke(instance, arguments);
                return Maybe.just(promise.future().result());
            } else {
                final Object returnValue = method.invoke(instance, args);
                if (Objects.isNull(returnValue)) {
                    return Maybe.empty();
                } else {
                    if (isEqualAnd(returnType, Single.class)) {
                        return Maybe.just((T) returnValue);
                    } else if (isEqualAnd(returnType, AsyncResult.class)) {
                        final AsyncResult<T> async = (AsyncResult<T>) returnValue;
                        final Promise<T> promise = Promise.promise();
                        promise.handle(async);
                        return Maybe.just(promise.future().result());
                    } else if (isEqualAnd(returnType, Handler.class)) {
                        return Maybe.just((T) returnValue);
                    } else {
                        final T returnT = (T) returnValue;
                        return Maybe.just(returnT);
                    }
                }
            }
        } catch (final Throwable ex) {
            ex.printStackTrace();
            return Maybe.error(ex);
        }
    }


    private static boolean isInterface(final JsonObject json) {
        final long count = json.fieldNames().stream().filter(FieldUtil::isInteger)
                .count();
        LOGGER.debug(MessageFormat.format("( isInterface Mode ) Parameter count: {0}, json: {1}", count, json.encode()));
        return count == json.fieldNames().size();
    }

    private static boolean isEqualAnd(final Class<?> clazz, final Class<?> interfaceCls) {
        return clazz == interfaceCls || ReflectionUtils.isMatch(clazz, interfaceCls);
    }
}
