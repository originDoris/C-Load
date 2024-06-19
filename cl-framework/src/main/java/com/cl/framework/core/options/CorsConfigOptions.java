package com.cl.framework.core.options;

import com.cl.framework.core.options.converter.CorsConfigOptionsConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 跨域配置
 *
 * @author xhz
 */
@Data
public class CorsConfigOptions {

    private transient Boolean credentials = Boolean.FALSE;

    private transient List<String> methods = new ArrayList<>();
    private transient List<String> headers = new ArrayList<>();
    private transient String origin;


    public CorsConfigOptions() {
    }


    public CorsConfigOptions(JsonObject jsonObject) {
        CorsConfigOptionsConverter.fromJson(jsonObject, this);
    }
    public CorsConfigOptions(Boolean credentials, List<String> methods, List<String> headers, String origin) {
        this.credentials = credentials;
        this.methods = methods;
        this.headers = headers;
        this.origin = origin;
    }
}
