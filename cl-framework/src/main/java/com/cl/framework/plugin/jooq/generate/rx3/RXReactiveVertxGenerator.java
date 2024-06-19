package com.cl.framework.plugin.jooq.generate.rx3;


import com.cl.framework.plugin.jooq.generate.builder.DelegatingVertxGenerator;
import com.cl.framework.plugin.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * RXJava vertx 生成器
 * @author xhz
 */
public class RXReactiveVertxGenerator extends DelegatingVertxGenerator {

    public RXReactiveVertxGenerator() {
        super(VertxGeneratorBuilder.init().withRX3API().withPostgresReactiveDriver().build());
    }
}
