package com.cl.framework.plugin.redis;

import io.reactivex.rxjava3.core.Maybe;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.redis.client.RedisAPI;
import io.vertx.rxjava3.redis.client.Response;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * redis 客户端持有者
 *
 * @author xhz
 */
public class RedisClientHolder {

    private RedisInfix redisInfix;

    public RedisClientHolder(RedisInfix redisInfix) {
        this.redisInfix = redisInfix;
    }

    private RedisAPI getRedis() {
        return redisInfix.getRedis();
    }

    private void send(Consumer<RedisAPI> consumer) {
        consumer.accept(getRedis());
    }

    public Maybe<Response> set(String key, String value) {
        return getRedis().set(List.of(key, value));
    }

    public Maybe<Response> expire(String key, String expire) {

        return getRedis().expire(List.of(key, expire));
    }

    public Maybe<Response> del(String key) {
        return getRedis().del(List.of(key));
    }

    public  Maybe<JsonObject> get(String key){
        return get(key, response -> {
            return response.toBuffer().toJsonObject();
        }, new JsonObject());
    }

    public  <T> Maybe<T> get(String key, Function<Response, T> function, T defaultValue) {
        return getRedis().exists(List.of(key)).flatMap(response -> {
            if (response.toBoolean()) {
                return getRedis().get(key).map(function::apply);
            } else {
                return Maybe.just(defaultValue);
            }
        });
    }
}
