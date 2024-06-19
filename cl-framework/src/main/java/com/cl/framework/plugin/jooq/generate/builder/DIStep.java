package com.cl.framework.plugin.jooq.generate.builder;

/**
 * 选择生成器 是否生成Dao层的注解
 * @author xhz
 */
public interface DIStep extends FinalStep{


    FinalStep withGuice(boolean generateGuiceModules, NamedInjectionStrategy namedInjectionStrategy);
}
