package com.cl.framework.core.utils;

import com.cl.framework.core.enums.ErrorCodeEnum;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.eventbus.Message;
import io.vertx.serviceproxy.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * @author xhz
 * HandleMessageUtil
 */
@Slf4j
public class HandleMessageUtil {

    public static <T> void handle(Single<T> asyncResult, Function<T, Object> function, Message<?> message) {
        Disposable disposable = asyncResult.subscribe(result -> {
            message.reply(function.apply(result));
        }, error -> error(message, error));
    }


    public static <T> void handle(Maybe<T> asyncResult, Function<T, Object> function, Message<?> message) {
        Disposable disposable = asyncResult.subscribe(result -> {
            message.reply(function.apply(result));
        }, error -> error(message, error));
    }


    public static <T> void handle(Observable<T> asyncResult, Function<T, Object> function, Message<?> message) {
        Disposable disposable = asyncResult.subscribe(result -> {
            message.reply(function.apply(result));
        }, error -> {
            error(message, error);
        });
    }


    public static <T,R> void handle(Maybe<T> asyncResult, Function<Object, AsyncResult<R>> function, Handler<AsyncResult<R>> handler) {
        Disposable subscribe = asyncResult.subscribe(result -> {
            handler.handle(function.apply(result));
        }, throwable -> {

            handlerError(handler, throwable);
        });
    }


    public static <T,R> void handle(Single<T> asyncResult, Function<T, AsyncResult<R>> function, Handler<AsyncResult<R>> handler) {
        Disposable subscribe = asyncResult.subscribe(result -> {
            handler.handle(function.apply(result));
        }, throwable -> {

            handlerError(handler, throwable);
        });
    }

    public static <T,R> void handle(Observable<T> asyncResult, Function<Object, AsyncResult<R>> function, Handler<AsyncResult<R>> handler) {
        Disposable subscribe = asyncResult.subscribe(result -> {
            handler.handle(function.apply(result));
        }, throwable -> {
            handlerError(handler, throwable);
        });
    }


    private static <R> void handlerError(Handler<AsyncResult<R>> handler, Throwable throwable) {
        throwable.printStackTrace();
        handler.handle(ServiceException.fail(ErrorCodeEnum.INTERNAL_ERROR.getCode(), throwable.getMessage()));
    }


    private static <R> void error(Message<?> message,Throwable throwable){
        log.info("error:", throwable);
        message.fail(ErrorCodeEnum.INTERNAL_ERROR.getCode(), throwable.getMessage());
    }



}
