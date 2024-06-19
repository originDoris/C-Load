package com.cl.framework.core.options.visitor;

import com.cl.framework.core.options.transformer.CliffTransformer;
import com.cl.framework.core.options.transformer.Transformer;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.secure.Cliff;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 表达式解析器
 *
 * @author xhz
 */
@Slf4j
public class CliffVisitor implements ConfigVisitor<Cliff> {
    public static final String KEY = "secure";

    private transient final Transformer<Cliff> cliffTransformer = ReflectionUtils.singleton(CliffTransformer.class);

    @Override
    public Cliff visit(String... keys) throws com.cl.framework.core.exception.DataTableException {
        JsonObject config = getConfig(KEY);
        log.info("加载配置文件前缀：{}, name:{},data:{}", KEY, this.getClass().getSimpleName(), config);
        return this.cliffTransformer.transform(config);
    }
}
