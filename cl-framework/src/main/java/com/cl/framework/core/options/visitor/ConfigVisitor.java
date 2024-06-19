package com.cl.framework.core.options.visitor;

import com.cl.framework.core.constants.StringsConstant;
import com.cl.framework.core.utils.config.ConfigTool;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 配置文件 - 访问者
 *
 * @author xhz
 */

public interface ConfigVisitor<T> {
    T visit(String... keys) throws com.cl.framework.core.exception.DataTableException;

    default JsonObject getConfig(String key){
        String env = System.getProperty(StringsConstant.ENV, StringsConstant.DEV);
        try {
            JsonObject data = ConfigTool.read(env, null);
            if (StringUtils.isBlank(key)) {
                return data;
            }
            return data.getJsonObject(key);
        } catch (IOException e) {
            throw new com.cl.framework.core.exception.DataTableException(e);
        }
    }
}
