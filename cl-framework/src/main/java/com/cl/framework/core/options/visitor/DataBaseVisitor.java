package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.DataBaseOptions;
import com.cl.framework.core.options.transformer.DataBaseTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;

/**
 * DataBaseVisitor
 *
 * @author xhz
 */
public class DataBaseVisitor implements ConfigVisitor<DataBaseOptions> {

    private transient final Transformer<DataBaseOptions> dataBaseOptionsTransformer = ReflectionUtils.singleton(DataBaseTransformer.class);

    @Override
    public DataBaseOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        return dataBaseOptionsTransformer.transform(getConfig(keys[0]));
    }
}
