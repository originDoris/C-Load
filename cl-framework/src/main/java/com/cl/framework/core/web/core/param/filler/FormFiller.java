package com.cl.framework.core.web.core.param.filler;

import com.cl.framework.core.runtime.DataTableSerializer;
import com.cl.framework.core.utils.FieldUtil;
import io.vertx.rxjava3.core.file.FileSystem;
import io.vertx.rxjava3.ext.web.FileUpload;
import io.vertx.rxjava3.ext.web.RoutingContext;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * .@FormParam
 *
 * 用来处理form表单的数据
 * @author xhz
 */
public class FormFiller implements Filler {

    @Override
    public Object apply(final String name, final Class<?> paramType, final RoutingContext context) {

        List<FileUpload> fileUploads = context.fileUploads();
        if (fileUploads.isEmpty()) {

            final String value = context.request().getFormAttribute(name);
            return DataTableSerializer.getValue(paramType, value);
        } else {
            final ConcurrentMap<String, Set<FileUpload>> compressed = FieldUtil.toFile(fileUploads);
            if (compressed.containsKey(name)) {

                final Set<FileUpload> uploadParam = compressed.get(name);
                final FileSystem fileSystem = context.vertx().fileSystem();
                return FieldUtil.toFile(new ArrayList<>(uploadParam), paramType, fileSystem::readFileBlocking);
            } else {
                final String value = context.request().getFormAttribute(name);
                return DataTableSerializer.getValue(paramType, value);
            }
        }
    }
}
