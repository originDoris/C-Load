package com.cl.framework.core.web.core.invoker;

import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.eventbus.Message;

import java.text.MessageFormat;

/**
 * 选择类型处理器
 * @author xhz
 */
public class JetSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(JetSelector.class);

    public static Invoker select(final Class<?> returnType,
                                 final Class<?> paramCls) {
        Invoker invoker = null;
        if (void.class == returnType || Void.class == returnType) {
            if (Envelop.class == paramCls) {
                invoker = ReflectionUtils.singleton(PingInvoker.class);
            } else if (Message.class.isAssignableFrom(paramCls)) {
                invoker = ReflectionUtils.singleton(MessageInvoker.class);
            }
        } else if (Envelop.class == returnType) {
            if (Envelop.class == paramCls) {
                invoker = ReflectionUtils.singleton(SyncInvoker.class);
            }
        } else if (Single.class.isAssignableFrom(returnType)) {
            invoker = ReflectionUtils.singleton(AsyncInvoker.class);
        } else {
            if (!Message.class.isAssignableFrom(paramCls)) {
                invoker = ReflectionUtils.singleton(DynamicInvoker.class);
            }
        }
        CubeFn.outError(LOGGER, null == invoker,
                DataTableException.class,
                ErrorCodeEnum.INVOKER_NULL_ERROR,
                MessageFormat.format(ErrorInfoConstant.INVOKER_NULL_ERROR, JetSelector.class, returnType, paramCls));
        return invoker;
    }
}
