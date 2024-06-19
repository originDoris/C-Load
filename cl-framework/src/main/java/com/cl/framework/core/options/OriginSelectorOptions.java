package com.cl.framework.core.options;

import com.cl.framework.core.options.converter.OriginSelectorOptionsConverter;
import io.vertx.core.json.JsonObject;
import lombok.Data;

/**
 * 源服务器路由配置
 *
 * @author xhz
 */
@Data
public class OriginSelectorOptions {


    private Integer port;

    private String host;

    private String regexPath;


    public OriginSelectorOptions(Integer port, String host, String regexPath) {
        this.port = port;
        this.host = host;
        this.regexPath = regexPath;
    }


    public OriginSelectorOptions() {
    }

    public OriginSelectorOptions(JsonObject jsonObject) {
        OriginSelectorOptionsConverter.fromJson(jsonObject, this);
    }
}
