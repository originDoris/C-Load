package com.cl.framework.core.options;

import com.cl.framework.core.options.converter.ProxyOptionsConverter;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.List;

/**
 * http代理配置
 *
 * @author xhz
 */
@Data
public class ProxyOptions {

    public static final Integer PORT = 80;

    private Integer port;

    private List<OriginSelectorOptions> origins;

    private List<String> authPath;

    private List<String> excludeAuthPath;

    private String rootPath;

    public ProxyOptions(Integer port, List<OriginSelectorOptions> origins, List<String> authPath, List<String> excludeAuthPath, String rootPath) {
        this.port = port;
        this.origins = origins;
        this.authPath = authPath;
        this.excludeAuthPath = excludeAuthPath;
        this.rootPath = rootPath;
    }

    public ProxyOptions() {
        this.port = PORT;
    }

    public ProxyOptions(JsonObject jsonObject) {
        ProxyOptionsConverter.fromJson(jsonObject, this);
    }

}
