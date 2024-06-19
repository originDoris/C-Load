package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.JsonUtil;
import io.vertx.core.json.JsonObject;

/**
 * @author xhz
 */
public class CommonSaber extends BaseFieldSaber {
    @Override
    public Object from(final Class<?> paramType,
                       final String literal) {
        return CubeFn.getDefault(null, () ->
                        CubeFn.getSemi(!FieldTypes.isSupport(paramType), getLogger(),
                                () -> JsonUtil.deserialize(literal, paramType),
                                () -> null),
                paramType, literal);
    }

    @Override
    public <T> Object from(final T input) {
        return CubeFn.getDefault(null, () -> {
            Object reference = null;
            if (!FieldTypes.isSupport(input.getClass())) {
                final String literal = JsonUtil.toJson(input);
                reference = new JsonObject(literal);
            }
            return reference;
        }, input);
    }
}
