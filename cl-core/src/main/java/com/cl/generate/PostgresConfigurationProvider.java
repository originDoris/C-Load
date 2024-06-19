package com.cl.generate;

import com.cl.framework.plugin.jooq.generate.VertxGeneratorStrategy;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.postgres.PostgresDatabase;

/**
 * pg数据库生成dao
 * @author xhz
 */

public class PostgresConfigurationProvider extends AbstractDatabaseConfigurationProvider {

    private static PostgresConfigurationProvider INSTANCE;
    public static PostgresConfigurationProvider getInstance() {
        return INSTANCE == null ? INSTANCE = new PostgresConfigurationProvider() : INSTANCE;
    }

    /**
     * 生成代码前执行
     */
    @Override
    public void setupDatabase() throws Exception {
    }

    @Override
    public Configuration createGeneratorConfig(String generatorName, String packageName, Class<? extends VertxGeneratorStrategy> generatorStrategy){
        Jdbc jdbcConfig = new Jdbc();
        jdbcConfig.setDriver("org.postgresql.Driver");
        jdbcConfig.setUrl("jdbc:postgresql://127.0.0.1:5432/cl");
        jdbcConfig.setUser("postgres");
        jdbcConfig.setPassword("159357");
        return createGeneratorConfig(generatorName, packageName, generatorStrategy, jdbcConfig, PostgresDatabase.class.getName());
    }

    @Override
    public org.jooq.Configuration createDAOConfiguration(){
        return new DefaultConfiguration().set(SQLDialect.POSTGRES);
    }

}
