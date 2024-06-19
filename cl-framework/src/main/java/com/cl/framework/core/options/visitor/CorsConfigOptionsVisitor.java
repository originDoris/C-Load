package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.CorsConfigOptions;
import com.cl.framework.core.options.transformer.CorsConfigOptionsTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 表达式解析器
 *
 * @author xhz
 */
@Slf4j
public class CorsConfigOptionsVisitor implements ConfigVisitor<CorsConfigOptions> {

    public static final String KEY = "cors";

    private transient final Transformer<CorsConfigOptions> corsConfigOptionsTransformer = ReflectionUtils.singleton(CorsConfigOptionsTransformer.class);


    @Override
    public CorsConfigOptions visit(String... keys) throws com.cl.framework.core.exception.DataTableException {

        JsonObject config = getConfig(KEY);
        log.info("加载配置文件前缀：{}, name:{},data:{}", KEY, this.getClass().getSimpleName(), config);
        return this.corsConfigOptionsTransformer.transform(config);
    }
}
