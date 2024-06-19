package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.JsonUtil;
import io.vertx.core.json.JsonArray;

/**
 * Collection
 * @author xhz
 */
public class CollectionSaber extends BaseFieldSaber {
    @Override
    public <T> Object from(final T input) {
        return CubeFn.getDefault(null,() -> {
            final String literal = JsonUtil.toJson(input);
            return new JsonArray(literal);
        }, input);
    }

    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return JsonUtil.deserialize(literal, paramType);
    }
}
