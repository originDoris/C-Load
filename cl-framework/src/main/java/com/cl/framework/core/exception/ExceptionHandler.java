package com.cl.framework.core.exception;

import com.cl.framework.core.domain.Result;
import com.cl.framework.core.enums.ErrorCodeEnum;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xhz
 * ExceptionHandler
 */
public class ExceptionHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);
    @Override
    public void handle(RoutingContext context) {
        Throwable failure = context.failure();
        LOG.info("===> exception fail: ", failure);

        failure.printStackTrace();
        if (failure instanceof NullPointerException) {

            context.json(new Result<>(ErrorCodeEnum.INTERNAL_ERROR.getCode(), "空指针异常错误."));
            return;
        }

        if (failure instanceof DataTableException) {
            DataTableException ex = (DataTableException) failure;
            context.json(new Result<>(ex.errorCodeEnum.getCode(), ex.getMessage()));
            return;
        }
        context.json(new Result<>(ErrorCodeEnum.INTERNAL_ERROR.getCode(), ErrorCodeEnum.INTERNAL_ERROR.getMessage()));
    }

   public static ExceptionHandler of(){
       return new ExceptionHandler();
   }
}
