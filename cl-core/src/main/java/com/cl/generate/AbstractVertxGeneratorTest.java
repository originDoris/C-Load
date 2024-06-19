package com.cl.generate;


import com.cl.framework.plugin.jooq.generate.VertxGenerator;
import com.cl.framework.plugin.jooq.generate.VertxGeneratorStrategy;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Logging;
import org.jooq.meta.jaxb.OnError;

/**
 * @author xhz
 */
public abstract class AbstractVertxGeneratorTest {

    private final Class<? extends VertxGenerator> generator;
    private final Class<? extends VertxGeneratorStrategy> strategy;
    private final String packageLocation;
    private final AbstractDatabaseConfigurationProvider configurationProvider;

    protected AbstractVertxGeneratorTest(Class<? extends VertxGenerator> generator, Class<? extends VertxGeneratorStrategy> strategy, String packageLocation, AbstractDatabaseConfigurationProvider configurationProvider) {
        this.generator = generator;
        this.strategy = strategy;
        this.packageLocation = packageLocation;
        this.configurationProvider = configurationProvider;
    }


    public void generateCodeShouldSucceed() throws Exception {
        try {
            configurationProvider.setupDatabase();
            Configuration configuration = configurationProvider.createGeneratorConfig(generator.getName(), packageLocation, strategy);
            configuration.setOnError(OnError.FAIL);
            configuration.setLogging(Logging.WARN);
            GenerationTool generationTool = new GenerationTool();
            generationTool.setDataSource(configurationProvider.getDataSource());
            generationTool.run(configuration);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
