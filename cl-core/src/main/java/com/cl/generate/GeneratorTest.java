package com.cl.generate;

import com.cl.framework.plugin.jooq.generate.VertxGeneratorStrategy;
import com.cl.framework.plugin.jooq.generate.rx3.RXReactiveVertxGenerator;

/**
 * 表达式解析器
 *
 * @author xhz
 */
public class GeneratorTest extends AbstractVertxGeneratorTest {
    protected GeneratorTest() {
        super(RXReactiveVertxGenerator.class, VertxGeneratorStrategy.class,"com.cl.repository", PostgresConfigurationProvider.getInstance());
    }

    public static void main(String[] args) throws Exception {
        GeneratorTest generatorTest = new GeneratorTest();
        generatorTest.generateCodeShouldSucceed();
    }
}
