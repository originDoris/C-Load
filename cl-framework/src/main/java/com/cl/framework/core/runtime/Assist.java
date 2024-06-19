package com.cl.framework.core.runtime;

import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava3.core.MultiMap;
import io.vertx.rxjava3.ext.auth.User;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.Session;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * envelop 辅助数据 以元数据为主
 *
 * @author xhz
 */
@Data
public class Assist implements Serializable {

    private Map<String, Object> context = new HashMap<>();

    private Session session;

    private User user;

    private HttpMethod method;

    private MultiMap headers;

    private String uri;

}
