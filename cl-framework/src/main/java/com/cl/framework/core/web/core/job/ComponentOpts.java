package com.cl.framework.core.web.core.job;

import com.cl.framework.core.utils.jackson.ClassDeserializer;
import com.cl.framework.core.utils.jackson.ClassSerializer;
import com.cl.framework.core.utils.jackson.JsonObjectDeserializer;
import com.cl.framework.core.utils.jackson.JsonObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import lombok.Getter;

import java.io.Serializable;

/**
 * yaml/json
 * @author xhz
 */
@Getter
public class ComponentOpts implements Serializable {
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private transient Class<?> component;
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private transient JsonObject config;

    public void setComponent(final Class<?> component) {
        this.component = component;
    }

    public void setConfig(final JsonObject config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "ComponentOpts{" +
                "component=" + this.component +
                ", config=" + this.config +
                '}';
    }
}
