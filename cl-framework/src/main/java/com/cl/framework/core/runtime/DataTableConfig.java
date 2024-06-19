package com.cl.framework.core.runtime;

import com.cl.framework.core.options.*;
import com.cl.framework.core.options.visitor.*;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.redis.client.RedisOptions;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * DataTable 运行时配置信息
 *
 * @author xhz
 */
@Slf4j
public class DataTableConfig {


    private static final VertxOptions VX_OPTS;

    private static final HazelcastClusterOptions CLUSTER;

    private static final PgConnectOptions PG_OPTIONS;

    public static final PoolOptions POOL_OPTIONS;

    public static final HttpServerOptions HTTP_SERVER_OPTIONS;

    public static final DataTableOptions DATA_CUBE_OPTIONS;

    public static final FlywayOptions FLYWAY_OPTIONS;

    public static final JWTAuthOptions JWT_AUTH_OPTIONS;

    public static final RedisOptions REDIS_OPTIONS;

    public static final ProxyOptions PROXY_OPTIONS;

    public static final CorsConfigOptions CORS_CONFIG_OPTIONS;

    private static final ConcurrentMap<Integer, HttpServerOptions> HTTP_SERVER_OPTS = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Integer, HttpServerOptions> SOCK_OPTS = new ConcurrentHashMap<>();

    public static final ScConfigOptions SC_CONFIG_OPTIONS;



    static {
        VertxConfigOptionsVisitor vertxConfigOptionsVisitor = ReflectionUtils.singleton(VertxConfigOptionsVisitor.class);
        VX_OPTS = vertxConfigOptionsVisitor.visit();

        HazelcastOptionsVisitor hazelcastOptionsVisitor = ReflectionUtils.singleton(HazelcastOptionsVisitor.class);
        CLUSTER = hazelcastOptionsVisitor.visit();

        PgConnectOptionsVisitor pgConnectOptionsVisitor = ReflectionUtils.singleton(PgConnectOptionsVisitor.class);
        PG_OPTIONS = pgConnectOptionsVisitor.visit();
        POOL_OPTIONS = pgConnectOptionsVisitor.getPoolOptions();

        HttpServerOptionsVisitor httpServerOptionsVisitor = ReflectionUtils.singleton(HttpServerOptionsVisitor.class);
        HTTP_SERVER_OPTIONS = httpServerOptionsVisitor.visit();

        DataTableOptionsVisitor dataCubeOptionsVisitor = ReflectionUtils.singleton(DataTableOptionsVisitor.class);
        DATA_CUBE_OPTIONS = dataCubeOptionsVisitor.visit();

        FlywayOptionsVisitor flywayOptionsVisitor = ReflectionUtils.singleton(FlywayOptionsVisitor.class);
        FLYWAY_OPTIONS = flywayOptionsVisitor.visit();

        JwtAuthOptionsVisitor jwtAuthOptionsVisitor = ReflectionUtils.singleton(JwtAuthOptionsVisitor.class);
        JWT_AUTH_OPTIONS = jwtAuthOptionsVisitor.visit();

        RedisOptionsVisitor redisOptionsVisitor = ReflectionUtils.singleton(RedisOptionsVisitor.class);
        REDIS_OPTIONS = redisOptionsVisitor.visit();

        ProxyOptionsVisitor proxyOptionsVisitor = ReflectionUtils.singleton(ProxyOptionsVisitor.class);
        PROXY_OPTIONS = proxyOptionsVisitor.visit();

        final ServerVisitor<HttpServerOptions> visitor = ReflectionUtils.singleton(HttpServerVisitor.class);
        HTTP_SERVER_OPTS.putAll(visitor.visit());

        CorsConfigOptionsVisitor corsConfigOptionsVisitor = ReflectionUtils.singleton(CorsConfigOptionsVisitor.class);
        CORS_CONFIG_OPTIONS = corsConfigOptionsVisitor.visit();

        ServerVisitor<HttpServerOptions> sockServerVisitor = ReflectionUtils.singleton(SockServerVisitor.class);
        SOCK_OPTS.putAll(sockServerVisitor.visit());

        ConfigVisitor<ScConfigOptions> scConfigVisitor = ReflectionUtils.singleton(ScConfigVisitor.class);
        SC_CONFIG_OPTIONS = scConfigVisitor.visit();
    }


    public static VertxOptions getVxOpts() {
        return VX_OPTS;
    }

    public static HazelcastClusterOptions getCluster() {
        return CLUSTER;
    }

    public static PgConnectOptions getPgOptions() {
        return PG_OPTIONS;
    }

    public static PoolOptions getPoolOptions() {
        return POOL_OPTIONS;
    }

    public static HttpServerOptions getHttpServerOptions() {
        return HTTP_SERVER_OPTIONS;
    }

    public static DataTableOptions getDataTableOptions() {
        return DATA_CUBE_OPTIONS;
    }

    public static FlywayOptions getFlywayOptions() {
        return FLYWAY_OPTIONS;
    }

    public static JWTAuthOptions getJwtAuthOptions() {
        return JWT_AUTH_OPTIONS;
    }

    public static RedisOptions getRedisOptions(){
        return REDIS_OPTIONS;
    }

    public static ProxyOptions getProxyOptions(){
        return PROXY_OPTIONS;
    }

    public static ConcurrentMap<Integer, HttpServerOptions> getHttpServerOpts(){
        return HTTP_SERVER_OPTS;
    }

    public static ConcurrentMap<Integer, HttpServerOptions> getSockOpts(){
        return SOCK_OPTS;
    }

    public static CorsConfigOptions getCorsConfigOptions(){
        return CORS_CONFIG_OPTIONS;
    }

    public static ScConfigOptions getScConfig(){
        return SC_CONFIG_OPTIONS;
    }
}
