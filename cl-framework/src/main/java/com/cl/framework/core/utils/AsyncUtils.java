package com.cl.framework.core.utils;

import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.ParamException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.plugin.jooq.rx3.ReactiveRXGenericQueryExecutor;
import com.cl.framework.plugin.jooq.rx3.ReactiveRXQueryExecutor;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Function;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.rxjava3.core.eventbus.Message;


/**
 * 异步工具类
 *
 * @author xhz
 */
public class AsyncUtils {

    public static <T> Single<T> single(final T entity) {
        return CubeFn.getDefault(Single.never(),
                () -> CubeFn.getSemi(entity instanceof Throwable, null,
                        () -> Single.error((Throwable) entity),
                        () -> Single.just(entity)),
                entity);
    }


    public static <T> Single<T> transactionClose(Single<Integer> single, ReactiveRXGenericQueryExecutor executor, Function<? super Integer, T> function) {
        return single.flatMap(integer -> executor.commit().toSingleDefault(function.apply(integer)))
                .doOnError(throwable -> {
                    executor.rollback().subscribe();
                    throwable.printStackTrace();
                    throw new ParamException(ErrorCodeEnum.PARAM_ERROR_CODE, throwable.getMessage());
                });
    }

   public static <T> BiConsumer<? super T, ? super Throwable> toConsumer(final Message<Envelop> message) {
       return (data, error) -> {
           if (error == null) {
               message.reply(ResultUtil.toEnvelop(data));
           } else {
               error.printStackTrace();
               message.reply(Envelop.failure(ResultUtil.toError(AsyncUtils.class, error)));
           }
       };
    }

    public static <T> Handler<AsyncResult<T>> toHandler(final Message<Envelop> message) {
        return handler -> {
            if (handler.succeeded()) {
                message.reply(ResultUtil.toEnvelop(handler.result()));
            } else {
                if (null != handler.cause()) {
                    handler.cause().printStackTrace();
                }
                message.reply(Envelop.failure(ResultUtil.toError(AsyncUtils.class, handler.cause())));
            }
        };
    }
}
