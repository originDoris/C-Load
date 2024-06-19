package com.cl.framework.core.web.core.agent;

import com.cl.framework.core.constants.Orders;
import com.cl.framework.core.constants.StringsConstant;
import io.vertx.core.http.HttpMethod;
import lombok.Data;
import lombok.Getter;

import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 从被@Endpoint 注解的类中的方法解析URI 为event
 *
 * @author xhz
 */
@Data
public class Event implements Serializable {
    @Getter
    private String path;

    private int order = Orders.EVENT;

    private Set<MediaType> consumes;

    private Set<MediaType> produces;

    private HttpMethod method;

    private Method action;

    private Object proxy;

    public void setPath(final String path) {
        if (null != path) {
            final String literal = path.trim();
            if (literal.endsWith(StringsConstant.SLASH)) {
                this.path = literal.substring(0, literal.length() - 1);
            } else {
                this.path = literal;
            }
        } else {
            this.path = path;
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "path='" + path + '\'' +
                ", order=" + order +
                ", consumes=" + consumes +
                ", produces=" + produces +
                ", method=" + method +
                ", action=" + action +
                ", proxy=" + proxy +
                '}';
    }
}
