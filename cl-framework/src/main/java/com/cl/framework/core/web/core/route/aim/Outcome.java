package com.cl.framework.core.web.core.route.aim;


import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.runtime.Envelop;

import com.cl.framework.core.web.core.route.adaptor.WingSelector;
import com.cl.framework.core.web.core.route.adaptor.Wings;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava3.core.http.HttpHeaders;
import io.vertx.rxjava3.core.http.HttpServerResponse;

import javax.ws.rs.core.MediaType;
import java.util.Objects;
import java.util.Set;


/**
 * Content-Type
 * Accept
 */
final class Outcome {



    static void media(final HttpServerResponse response, final Set<MediaType> produces) {
        if (produces.isEmpty()) {
            response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        } else {
            if (produces.contains(MediaType.WILDCARD_TYPE)) {
                response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            } else {
                final MediaType type = produces.iterator().next();
                if (Objects.isNull(type)) {
                    response.putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
                } else {
                    final String content = type.getType() + "/" + type.getSubtype();
                    response.putHeader(HttpHeaders.CONTENT_TYPE, content);
                }
            }
        }
    }

    static void security(final HttpServerResponse response) {
//                 不允许代理缓存数据
        response.putHeader("Cache-Control", "no-store, no-cache")
                .putHeader("X-Content-Type-Options", "nosniff")
                // 严格的HTTPS  (大约6个月)
                .putHeader("Strict-Transport-Security", "max-age=" + 15768000)
                // IE8+ 不允许在此资源的上下文中打开附件
                .putHeader("X-Download-Options", "noopen")
                // 启动XSS
                .putHeader("X-XSS-Protection", "1; mode=block")
                .putHeader("X-FRAME-OPTIONS", "DENY");
    }

    static void out(final HttpServerResponse response, final Envelop envelop, final Set<MediaType> produces) {

        // 设置请求响应内容
        if (!response.ended()) {
            if (HttpMethod.HEAD == envelop.getAssist().getMethod()) {
                response.setStatusCode(ErrorCodeEnum.NO_CONTENT.getCode());
                response.setStatusMessage(ErrorCodeEnum.NO_CONTENT.getMessage());
                response.end("").subscribe();
            } else {
                final String headerStr = response.headers().get(HttpHeaders.CONTENT_TYPE);
                final Wings wings = WingSelector.end(headerStr, produces);
                response.setStatusMessage("OK");
                wings.output(response, envelop);
            }
        }
        response.closed();
    }



}
