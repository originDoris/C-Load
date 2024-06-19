package com.cl.framework.core.options.transformer;

import io.vertx.core.json.JsonObject;

/**
 * PoolOptions 转换器
 *
 * @author xhz
 */
public class DataTableOptionsTransformer implements Transformer<com.cl.framework.core.options.DataTableOptions>{
    @Override
    public com.cl.framework.core.options.DataTableOptions transform(JsonObject input) {
        return null == input ? new com.cl.framework.core.options.DataTableOptions() : new com.cl.framework.core.options.DataTableOptions(input);
    }
}
