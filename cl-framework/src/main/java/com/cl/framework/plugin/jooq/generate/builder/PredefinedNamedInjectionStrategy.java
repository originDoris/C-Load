package com.cl.framework.plugin.jooq.generate.builder;

import java.util.function.UnaryOperator;


/**
 * 预定义命名注入策略
 * @author xhz
 */
public enum PredefinedNamedInjectionStrategy implements NamedInjectionStrategy {

    DISABLED(s->""),

    SCHEMA(s->String.format("@javax.inject.Named(\"%s\")",s.toUpperCase()))
    ;

    private final UnaryOperator<String> op;

    PredefinedNamedInjectionStrategy(UnaryOperator<String> op) {
        this.op = op;
    }

    @Override
    public String apply(String s) {
        return op.apply(s);
    }
}
