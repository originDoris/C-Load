package com.cl.generate;

import com.cl.framework.plugin.jooq.generate.VertxGeneratorStrategy;
import com.cl.framework.plugin.jooq.shared.postgres.JSONBToJsonObjectConverter;
import io.vertx.core.json.JsonObject;
import org.jooq.meta.jaxb.*;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jensklingsporn on 13.02.18.
 */
abstract class AbstractDatabaseConfigurationProvider {

    private static final String TARGET_FOLDER = "/Users/origindoris/Documents/me/C-Load/cl-core/src/main/java/";

    static{
        System.setProperty("vertx.logger-delegate-factory-class-name","io.vertx.core.logging.SLF4JLogDelegateFactory");
    }

    public abstract void setupDatabase() throws Exception;

    public abstract Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy);

    public abstract org.jooq.Configuration createDAOConfiguration();

    Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy, Jdbc config, String dbType){



        Configuration configuration = new Configuration();
        Database databaseConfig = new Database();
        databaseConfig.setName(dbType);

        databaseConfig.setInputSchema("public");
        databaseConfig.setOutputSchemaToDefault(false);
        databaseConfig.setOutputCatalogToDefault(false);
        databaseConfig.setIncludes("cl_code_config");
        databaseConfig.setForcedTypes(getForcedTypes());

        Target targetConfig = new Target();
        targetConfig.setPackageName(packageName);
        targetConfig.setDirectory(TARGET_FOLDER);

        Generate generateConfig = new Generate();
        generateConfig.setInterfaces(true);
        generateConfig.setPojos(true);
        generateConfig.setFluentSetters(true);
        generateConfig.setDaos(true);
        generateConfig.setPojosEqualsAndHashCode(true);
        generateConfig.setJavaTimeTypes(true);
        generateConfig.setGeneratedAnnotation(false);

        // 自定义策略 让dao从AbstractVertxDAO扩展
        Strategy strategy = new Strategy();
        strategy.setName(generatorStrategy.getName());

        Generator generatorConfig = new Generator();
        generatorConfig.setName(generatorName);
        generatorConfig.setDatabase(databaseConfig);
        generatorConfig.setTarget(targetConfig);
        generatorConfig.setGenerate(generateConfig);
        generatorConfig.setStrategy(strategy);
        configuration.setGenerator(generatorConfig);

        configuration.setJdbc(config);

        configuration.setLogging(Logging.FATAL);

        return configuration;
    }

    public DataSource getDataSource() {
        return null;
    }


    public List<ForcedType> getForcedTypes(){
//        ForcedType jsonObjectType = new ForcedType();
//        jsonObjectType.setUserType(BigInteger.class.getName());
//        jsonObjectType.setConverter(NumericToLongConverter.class.getName());
//        jsonObjectType.setBinding(NumericToLongBinding.class.getName());
//        jsonObjectType.setIncludeExpression("id");
//        jsonObjectType.setIncludeTypes(".*");


//        ForcedType jsonObjectType2 = new ForcedType();
//        jsonObjectType.setUserType(Long.class.getName());
//        jsonObjectType.setBinding(NumericToLongBinding.class.getName());
//        jsonObjectType.setIncludeExpression("id");
//        jsonObjectType.setIncludeTypes(".*");

//        jsonArray
//        ForcedType jsonArrayType = new ForcedType();
//        jsonArrayType.setUserType(JsonArray.class.getName());
//        jsonArrayType.setConverter(JsonArrayConverter.class.getName());
//        jsonArrayType.setIncludeExpression("someJsonArray");
//        jsonArrayType.setIncludeTypes(".*");

        /**
//         * jsonB column to JsonObject
//         */
//        ForcedType objectToJsonObjectBinding = new ForcedType();
//        objectToJsonObjectBinding.setUserType(JsonObject.class.getName());
//        objectToJsonObjectBinding.setConverter(JSONBToJsonObjectConverter.class.getName());
//        objectToJsonObjectBinding.setIncludeExpression("view_config");
//
//        ForcedType objectToJsonObjectBinding2 = new ForcedType();
//        objectToJsonObjectBinding2.setUserType(JsonObject.class.getName());
//        objectToJsonObjectBinding2.setConverter(JSONBToJsonObjectConverter.class.getName());
//        objectToJsonObjectBinding2.setIncludeExpression("field_property");
//
//        return Arrays.asList(objectToJsonObjectBinding,objectToJsonObjectBinding2);
        return Arrays.asList();
    }
}
