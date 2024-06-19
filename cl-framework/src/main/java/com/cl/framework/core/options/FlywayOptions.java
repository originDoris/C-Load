package com.cl.framework.core.options;

import com.cl.framework.core.options.converter.FlywayOptionsConverter;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.HashMap;

/**
 * flyway 配置
 *
 * @author xhz
 */
@Data
public class FlywayOptions {

    private boolean enable;

    private String url;

    private String user;

    private String password;

    private String locations;

    private String baselineOnMigrate;

    private String table;

    private Boolean cleanDisabled;

    private Integer baselineVersion;

    private Boolean validateOnMigrate;


    public FlywayOptions(final JsonObject json) {
        this();
        FlywayOptionsConverter.fromJson(json, this);
    }

    public FlywayOptions() {
    }

    public HashMap<String, String> getMap(){
        return FlywayOptionsConverter.fromMap(this);
    }

}
