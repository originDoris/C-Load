package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.annotation.Address;
import com.cl.framework.core.annotation.Consumer;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.funcation.CubeFn;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.text.MessageFormat;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 扫描所有被@Queue的注解
 *
 * @author xhz
 */
public class ConsumerInquirer implements Inquirer<Set<Class<?>>> {


    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerInquirer.class);

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> classes) {
        final Set<Class<?>> queues = classes.stream()
                .filter((item) -> item.isAnnotationPresent(Consumer.class))
                .collect(Collectors.toSet());
        LOGGER.info(MessageFormat.format(MessageConstant.SCAN_CONSUMER, queues.size()));
        ensure(queues);
        return queues;
    }

    private void ensure(final Set<Class<?>> clazzes) {
        Observable.fromIterable(clazzes)
                .map(Class::getDeclaredMethods)
                .flatMap(Observable::fromArray)
                .filter(method -> method.isAnnotationPresent(Address.class))
                .subscribe(method -> {
                    final Class<?> returnType = method.getReturnType();
                    final Class<?> parameterTypes = method.getParameterTypes()[0];
                    if (Message.class.isAssignableFrom(parameterTypes)) {
                        CubeFn.outError(LOGGER, void.class != returnType && Void.class != returnType,
                                DataTableException.class, ErrorCodeEnum.ADDRESS_METHOD_ERROR, MessageFormat.format(ErrorInfoConstant.ADDRESS_METHOD_NOT_VOID_ERROR, method.getName()));
                    } else {
                        CubeFn.outError(LOGGER, void.class == returnType || Void.class == returnType,
                                DataTableException.class, ErrorCodeEnum.ADDRESS_METHOD_ERROR, MessageFormat.format(ErrorInfoConstant.ADDRESS_METHOD_IS_VOID_ERROR, method.getName()));
                    }
                })
                .dispose();
    }
}
