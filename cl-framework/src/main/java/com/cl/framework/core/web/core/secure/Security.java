package com.cl.framework.core.web.core.secure;

import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 为wall组件定义的接口
 * @author xhz
 */
public interface Security {
    /**
     * 登录成功后将令牌存在哪
     * 1. redis
     * 2. database
     * 3. etcd
     * 4. 自定义
     *
     * @param data Stored token information
     */
    default Single<JsonObject> store(final JsonObject data) {
        return Single.just(data);
    }

    /**
     * 401 Access,验证令牌
     */
    Single<Boolean> verify(JsonObject data);

    /**
     *  403 Access, 验证资源访问权限
     */
    default Single<Boolean> access(final JsonObject user) {
        return Single.just(Boolean.TRUE);
    }
}
