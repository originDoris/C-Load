package com.cl.framework.core.web.core.param.resolver;


import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.web.core.param.ParamContainer;
import io.vertx.rxjava3.core.file.FileSystem;
import io.vertx.rxjava3.ext.web.FileUpload;
import io.vertx.rxjava3.ext.web.RoutingContext;


import java.util.List;

/**
 * form参数转换
 * @author xhz
 */
public class FormResolver<T> implements Resolver<T> {

    @Override
    public ParamContainer<T> resolve(final RoutingContext context, final ParamContainer<T> income) {
        List<FileUpload> fileUploads = context.fileUploads();
        final Class<?> argType = income.getArgType();
        final FileSystem fileSystem = context.vertx().fileSystem();
        final T result = FieldUtil.toFile(fileUploads, argType, fileSystem::readFileBlocking);
        income.setValue(result);
        return income;
    }
}
