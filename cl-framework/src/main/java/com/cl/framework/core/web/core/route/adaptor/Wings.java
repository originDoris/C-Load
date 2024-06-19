package com.cl.framework.core.web.core.route.adaptor;

import com.cl.framework.core.runtime.Envelop;
import io.vertx.rxjava3.core.http.HttpServerResponse;


/**
 * 根据Content-Type 构建不同的返回值
 * @author xhz
 */
public interface Wings {


    void output(HttpServerResponse response, Envelop envelop);
}
