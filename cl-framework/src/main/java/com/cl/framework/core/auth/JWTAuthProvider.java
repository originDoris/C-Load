package com.cl.framework.core.auth;

import com.cl.framework.core.vertx.VertxLauncher;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.rxjava3.ext.auth.User;
import io.vertx.rxjava3.ext.auth.jwt.JWTAuth;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述：JWT Token的提供者
 */
@Slf4j
public class JWTAuthProvider {



    private static JWTAuth instance;

    private JWTAuthProvider() {
    }

    private static JWTAuth provider(){
        return provider(null);
    }

    private synchronized static JWTAuth provider(JWTAuthOptions options) {
        if (options == null) {
            options =  com.cl.framework.core.runtime.DataTableConfig.getJwtAuthOptions();
        }
        if (instance == null) {
            instance = JWTAuth.create(VertxLauncher.getVertx(), options);
        }
        return instance;
    }

    private static JWTOptions createDefaultJWTOptions(){
        return com.cl.framework.core.runtime.DataTableConfig.getJwtAuthOptions().getJWTOptions();
    }

    public static String makeJwtToken(JsonObject jsonObject) {
        return makeJwtToken(jsonObject, createDefaultJWTOptions());
    }
    public static String makeJwtToken(JsonObject jsonObject, JWTOptions options) {
        return provider().generateToken(jsonObject, options);
    }

    public static Single<User> parse(String token) {
        return provider().authenticate(new TokenCredentials(token));
    }

    public static JWTAuth getInstance(JsonObject jsonObject){
        return provider(new JWTAuthOptions(jsonObject));
    }


}
