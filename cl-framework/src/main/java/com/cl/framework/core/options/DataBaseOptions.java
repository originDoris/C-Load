package com.cl.framework.core.options;

import com.cl.framework.core.enums.DatabaseType;
import com.cl.framework.core.options.converter.DataBaseConverter;
import com.cl.framework.core.utils.jackson.JsonObjectDeserializer;
import com.cl.framework.core.utils.jackson.JsonObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.Objects;

/**
 * 表达式解析器
 *
 * @author xhz
 */
@Data
public class DataBaseOptions {

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private transient JsonObject options = new JsonObject();
    /**
     * Database host name
     */
    private transient String hostname;

    /**
     * Database instance name
     */
    private transient String instance;

    /**
     * Database port number
     */
    private transient Integer port;

    /**
     * Database category
     */
    private transient DatabaseType category;

    /**
     * JDBC connection string
     */
    private transient String jdbcUrl;

    /**
     * Database username
     */
    private transient String username;

    /**
     * Database password
     */
    private transient String password;

    /**
     * Database driver class
     */
    private transient String driverClassName;


    public DataBaseOptions() {
    }

    public DataBaseOptions(JsonObject jsonObject) {
        DataBaseConverter.fromJson(jsonObject, this);
    }

    public <T> T getOption(final String optionKey) {
        final JsonObject options = this.options;
        final Object value = options.getValue(optionKey);
        return Objects.isNull(value) ? null : (T) value;
    }

    public <T> T getOption(final String optionKey, final T defaultValue) {
        final T result = this.getOption(optionKey);
        return Objects.isNull(result) ? defaultValue : result;
    }


}
