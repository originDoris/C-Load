package com.cl.framework.core.utils.secure;


import io.vertx.core.impl.logging.Logger;
import io.vertx.core.json.JsonObject;


/**
 * 鉴权相关日志
 * @author xhz
 */
public class Sc {

    public static void infoAuth(final Logger logger, final String pattern, final Object... args) {
        ScLog.infoAuth(logger, pattern, args);
    }

    public static JsonObject jwtToken(final JsonObject data) {
        return ScToken.jwtToken(data);
    }


}
