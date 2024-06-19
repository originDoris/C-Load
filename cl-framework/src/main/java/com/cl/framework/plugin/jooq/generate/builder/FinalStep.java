package com.cl.framework.plugin.jooq.generate.builder;

/**
 * 构建代码生成器的步骤
 * @author xhz
 */
public interface FinalStep {

    ComponentBasedVertxGenerator build();

    ComponentBasedVertxGenerator build(BuildOptions buildOptions);
}
