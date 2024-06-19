package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.ScConfigOptions;
import com.cl.framework.core.options.transformer.ScConfigOptionsTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.json.JsonObject;

/**
 * 表达式解析器
 *
 * @author xhz
 */
public class ScConfigVisitor implements ConfigVisitor<ScConfigOptions>{

    public static final String KEY = "sc";

    private transient final Transformer<ScConfigOptions> scConfigOptionsTransformer = ReflectionUtils.singleton(ScConfigOptionsTransformer.class);

    @Override
    public ScConfigOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY);
        if (config == null) {
            return new ScConfigOptions();
        }
        return scConfigOptionsTransformer.transform(config);
    }
}
