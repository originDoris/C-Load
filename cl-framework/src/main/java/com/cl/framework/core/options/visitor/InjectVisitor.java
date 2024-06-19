package com.cl.framework.core.options.visitor;

import io.vertx.core.json.JsonObject;

/**
 * 插件配置读取
 *
 * @author xhz
 */
public class InjectVisitor implements ConfigVisitor<JsonObject> {
    @Override
    public JsonObject visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        return getConfig(keys[0]);
    }
}
