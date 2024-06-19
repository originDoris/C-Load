package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.transformer.JwtAuthOptionsTransformer;
import com.cl.framework.core.options.transformer.JwtOptionsTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

/**
 * JwtAuthOptionsVisitor
 *
 * @author xhz
 */
public class JwtAuthOptionsVisitor implements ConfigVisitor<JWTAuthOptions>{

    public static final String KEY = "jwt";

    public static final String AUTH_KEY = "auth";


    private transient final Transformer<JWTAuthOptions> jwtAuthOptionsTransformer = ReflectionUtils.singleton(JwtAuthOptionsTransformer.class);
    private transient final Transformer<JWTOptions> jwtOptionsTransformer = ReflectionUtils.singleton(JwtOptionsTransformer.class);
    @Override
    public JWTAuthOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY);
        if (config == null || !config.containsKey(AUTH_KEY) || config.getJsonObject(AUTH_KEY) == null) {
            return new JWTAuthOptions();
        }
        JsonObject authJson = config.getJsonObject(AUTH_KEY);


        JWTOptions jwtOptions = jwtOptionsTransformer.transform(config);
        JWTAuthOptions jwtAuthOptions = jwtAuthOptionsTransformer.transform(authJson);
        jwtAuthOptions.setJWTOptions(jwtOptions);
        return jwtAuthOptions;
    }
}
