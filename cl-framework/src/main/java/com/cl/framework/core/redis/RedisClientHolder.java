package com.cl.framework.core.redis;

import com.cl.framework.core.vertx.VertxLauncher;

import io.reactivex.rxjava3.core.Maybe;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.redis.client.Redis;
import io.vertx.rxjava3.redis.client.RedisAPI;
import io.vertx.rxjava3.redis.client.Response;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * redis 客户端持有者
 *
 * @author xhz
 */
public class RedisClientHolder {

    private static final AtomicReference<CompletableFuture<RedisAPI>> REDIS_FUTURE = new AtomicReference<>();
    private static final AtomicReference<RedisAPI> REDIS = new AtomicReference<>();


    public static RedisAPI getRedis() {
        if (REDIS.get() == null) {
            RedisAPI api = RedisAPI.api(Redis.createClient(VertxLauncher.getVertx(), com.cl.framework.core.runtime.DataTableConfig.getRedisOptions()));
            REDIS.set(api);
        }
        return REDIS.get();
    }


    private static void send(Consumer<RedisAPI> consumer){
        consumer.accept(getRedis());
    }

    public static Maybe<Response> set(String key, String value) {
        return getRedis().set(List.of(key, value));
    }

    public static Maybe<Response> expire(String key, String expire) {

        return getRedis().expire(List.of(key, expire));
    }

    public static Maybe<Response> del(String key) {
        return getRedis().del(List.of(key));
    }

    public static Maybe<Response> del(List<String> key) {
        return getRedis().del(key);
    }

    public static Maybe<JsonObject> get(String key){
        return get(key, response -> {
            return response.toBuffer().toJsonObject();
        }, new JsonObject());
    }

    public static <T> Maybe<T> get(String key, Function<Response, T> function,T defaultValue) {
        return getRedis().exists(List.of(key)).flatMap(response -> {
            if (response.toBoolean()) {
                return getRedis().get(key).map(function::apply);
            } else {
                return Maybe.just(defaultValue);
            }
        });
    }
}
