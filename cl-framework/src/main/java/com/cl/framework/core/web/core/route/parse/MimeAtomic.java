package com.cl.framework.core.web.core.route.parse;

import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.enums.ResolverType;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.exception.WebException;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.param.ParamContainer;
import com.cl.framework.core.web.core.param.resolver.Resolver;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.ext.web.RoutingContext;

import javax.ws.rs.core.HttpHeaders;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;

/**
 *
 *
 * 在执行代码之前处理请求数据
 *
 * @author xhz
 * @param <T> generic definition
 */
public class MimeAtomic<T> implements Atomic<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MimeAtomic.class);

    @Override
    public ParamContainer<T> ingest(final RoutingContext context,
                                    final ParamContainer<T> income) throws WebException {
        final ParamContainer<T> epsilon;
        if (ResolverType.TYPED == income.getType()) {
            final Atomic<T> atomic = ReflectionUtils.singleton(TypedAtomic.class);
            epsilon = atomic.ingest(context, income);
        } else if (ResolverType.STANDARD == income.getType()) {
            final Atomic<T> atomic = ReflectionUtils.singleton(StandardAtomic.class);
            epsilon = atomic.ingest(context, income);
        } else {
            final Resolver<T> resolver = this.getResolver(context, income);
            epsilon = resolver.resolve(context, income);
        }
        return epsilon;
    }

    private Resolver<T> getResolver(final RoutingContext context,
                                    final ParamContainer<T> income) {
        final Annotation annotation = income.getAnnotation();
        final Class<?> resolverCls = ReflectionUtils.invoke(annotation, "resolver");

        CubeFn.outError(LOGGER,resolverCls == null,
                DataTableException.class, ErrorCodeEnum.RESOLVER_NULL_ERROR, ErrorInfoConstant.RESOLVER_NULL_ERROR);

        final String header = context.request().getHeader(HttpHeaders.CONTENT_TYPE);

        LOGGER.info(MessageFormat.format(MessageConstant.RESOLVER_CONFIG, resolverCls, header));

        CubeFn.outError(LOGGER, !ReflectionUtils.isMatch(resolverCls, Resolver.class),
                DataTableException.class, ErrorCodeEnum.RESOLVER_TYPE_ERROR, MessageFormat.format(ErrorInfoConstant.RESOLVER_NULL_ERROR, resolverCls));

        return (Resolver) ReflectionUtils.singleton(resolverCls);

    }
}
