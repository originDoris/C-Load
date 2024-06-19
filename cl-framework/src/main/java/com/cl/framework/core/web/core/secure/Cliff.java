package com.cl.framework.core.web.core.secure;

import com.cl.framework.core.enums.WallType;
import com.cl.framework.core.utils.FieldUtil;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 扫描@Wall 注解的元数据
 * @author xhz
 */
@AllArgsConstructor
@NoArgsConstructor
public class Cliff implements Serializable, Comparable<Cliff> {
    /**
     * defined = false
     * Standard Authorization
     */
    private final Phylum authorizer = new Phylum();
    /**
     * defined = true
     * Custom Authorization
     */
    private final Ostium authorization = new Ostium();
    /**
     * 需要鉴权的路径
     */
    private String path;

    private int order;

    private JsonObject config;
    /**
     * Current wall type
     */
    private WallType type;
    /**
     * Proxy instance
     */
    private Object proxy;
    /**
     * User-Defined authorization
     */
    private boolean defined = false;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliff)) {
            return false;
        }
        final Cliff wall = (Cliff) o;
        return this.order == wall.order &&
                Objects.equals(this.path, wall.path) &&
                this.type == wall.type &&
                Objects.equals(this.proxy, wall.proxy);
    }

    @Override
    public int compareTo(final Cliff target) {
        return FieldUtil.compareTo(this, target, (left, right) -> {
            int result = FieldUtil.compareTo(left.getPath(), right.getPath());
            if (0 == result) {
                result = FieldUtil.compareTo(left.getOrder(), right.getOrder());
            }
            return result;
        });
    }


    public Phylum getAuthorizer() {
        return authorizer;
    }

    public Ostium getAuthorization() {
        return authorization;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public JsonObject getConfig() {
        return config;
    }

    public void setConfig(JsonObject config) {
        this.config = config;
    }

    public WallType getType() {
        return type;
    }

    public void setType(WallType type) {
        this.type = type;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }

    public boolean isDefined() {
        return defined;
    }

    public void setDefined(boolean defined) {
        this.defined = defined;
    }
}
