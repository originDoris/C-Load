package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.ProxyOptions;
import com.cl.framework.core.options.transformer.ProxyOptionsTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.json.JsonObject;


/**
 * ProxyOptionsVisitor
 *
 * @author xhz
 */
public class ProxyOptionsVisitor implements ConfigVisitor<ProxyOptions> {

    public static final String KEY = "proxy";

    private transient final Transformer<ProxyOptions> proxyOptionsTransformer = ReflectionUtils.singleton(ProxyOptionsTransformer.class);

    @Override
    public ProxyOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY);
        if (config == null) {
            return new ProxyOptions();
        }
        return proxyOptionsTransformer.transform(config);
    }
}
