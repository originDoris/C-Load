package com.cl.framework.core.utils.secure;


import com.cl.framework.core.utils.jackson.JsonArrayDeserializer;
import com.cl.framework.core.utils.jackson.JsonArraySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author xhz
 */
@Getter
public class ScCondition implements Serializable {
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray user;

    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray role;

    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray group;
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray action;
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray permission;
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray resource;

    public void setUser(final JsonArray user) {
        this.user = user;
    }

    public void setRole(final JsonArray role) {
        this.role = role;
    }

    public void setGroup(final JsonArray group) {
        this.group = group;
    }

    public void setAction(final JsonArray action) {
        this.action = action;
    }

    public void setPermission(final JsonArray permission) {
        this.permission = permission;
    }

    public void setResource(final JsonArray resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "ScCondition{" +
                "user=" + this.user +
                ", role=" + this.role +
                ", group=" + this.group +
                ", action=" + this.action +
                ", permission=" + this.permission +
                ", resource=" + this.resource +
                '}';
    }
}
