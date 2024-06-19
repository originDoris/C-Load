package com.cl.framework.core.handler;

import com.cl.framework.core.domain.Result;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.ApiExecException;
import com.cl.framework.core.exception.ParamException;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author xhz
 * @Description: http响应处理
 */
public abstract class AbstractHttpHandler extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());




    protected <T> void sendApiExecResponse(RoutingContext context, Single<T> asyncResult,
                                    Function<T, String> converter, BiConsumer<RoutingContext, String> f) {
        if (asyncResult == null) {
            internalError(context, "invalid_status");
        } else {
            Disposable subscribe = asyncResult.subscribe(r -> {
                f.accept(context, converter.apply(r));
            }, ex -> {
                ex.printStackTrace();
                LOG.info("error info ，", ex);
                apiExecError(context, ex);
            });
        }
    }


    protected <T> void sendResponse(RoutingContext context, Single<T> asyncResult,
                                    Function<T, String> converter, BiConsumer<RoutingContext, String> f) {
        if (asyncResult == null) {
            internalError(context, "invalid_status");
        } else {
            Disposable subscribe = asyncResult.subscribe(r -> {
                f.accept(context, converter.apply(r));
            }, ex -> {
                ex.printStackTrace();
                LOG.info("error info ，", ex);
                if (ex instanceof ParamException) {
                    paramError(context, (ParamException) ex);
                    return;
                }
                if (ex instanceof ApiExecException) {
                    apiExecError(context, (ApiExecException) ex);
                    return;
                }
                internalError(context, ex);
            });
        }
    }

    /**
     * Send back a response with status 200 Created.
     *
     * @param context routing context
     * @param content body content in JSON format
     */
    protected void created(RoutingContext context, String content) {
        context.response().setStatusCode(ErrorCodeEnum.SUCCESS.getCode())
                .putHeader("content-type", "application/json")
                .end(content);
    }



    /**
     * Send back a response with status 500 Internal Error.
     *
     * @param context routing context
     * @param ex      exception
     */
    protected void internalError(RoutingContext context, Throwable ex) {
        context.response().setStatusCode(ErrorCodeEnum.SUCCESS.getCode())
                .putHeader("content-type", "application/json")
                .end(JsonObject.mapFrom(new Result<>(ErrorCodeEnum.INTERNAL_ERROR.getCode(), ex.getMessage())).encodePrettily());
    }


    protected void paramError(RoutingContext context, com.cl.framework.core.exception.DataTableException ex) {
        context.response().setStatusCode(ErrorCodeEnum.SUCCESS.getCode())
                .putHeader("content-type", "application/json")
                .end(JsonObject.mapFrom(new Result<>(ex.getErrorCodeEnum().getCode(), ex.getMessage())).encodePrettily());
    }


    protected void apiExecError(RoutingContext context, Throwable ex) {
        context.response().setStatusCode(ErrorCodeEnum.SUCCESS.getCode())
                .putHeader("content-type", "application/json")
                .end(JsonObject.mapFrom(new Result<>( ex.getMessage(), ex.getMessage())).encodePrettily());
    }

    /**
     * Send back a response with status 500 Internal Error.
     *
     * @param context routing context
     * @param cause   error message
     */
    protected void internalError(RoutingContext context, String cause) {
        context.response().setStatusCode(500)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("error", cause).encodePrettily());
    }
}
