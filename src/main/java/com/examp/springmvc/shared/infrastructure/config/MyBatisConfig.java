package com.examp.springmvc.shared.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.flywaydb.core.Flyway;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource(value = "classpath:application-local.properties", ignoreResourceNotFound = true)
})
@MapperScan({
    "com.examp.springmvc.user.infrastructure.mapper",
    "com.examp.springmvc.catalog.infrastructure.mapper",
    "com.examp.springmvc.order.infrastructure.mapper",
    "com.examp.springmvc.shared.infrastructure.task"
})
public class MyBatisConfig {

    private final Environment environment;

    @Autowired
    public MyBatisConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName(environment.getRequiredProperty("db.driver-class-name"));

        config.setJdbcUrl(environment.getRequiredProperty("db.url"));

        config.setUsername(environment.getRequiredProperty("db.username"));

        config.setPassword(environment.getRequiredProperty("db.password"));

        config.setPoolName(environment.getRequiredProperty("db.pool.pool-name"));

        config.setMinimumIdle(Integer.parseInt(environment.getRequiredProperty("db.pool.minimum-idle")));

        config.setMaximumPoolSize(Integer.parseInt(environment.getRequiredProperty("db.pool.maximum-pool-size")));

        config.setConnectionTimeout(Long.parseLong(environment.getRequiredProperty("db.pool.connection-timeout")));

        config.setIdleTimeout(Long.parseLong(environment.getRequiredProperty("db.pool.idle-timeout")));

        config.setMaxLifetime(Long.parseLong(environment.getRequiredProperty("db.pool.max-lifetime")));

        config.setConnectionTestQuery("SELECT 1 FROM DUAL");

        // Allow application context to fail fast by default unless overridden
        String initTimeout = environment.getProperty("db.pool.initialization-fail-timeout");
        if (initTimeout != null) {
            config.setInitializationFailTimeout(Long.parseLong(initTimeout));
        } else {
            config.setInitializationFailTimeout(1);
        }

        return new HikariDataSource(config);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .locations("classpath:db/migration")
                .load();
    }

    @Bean
    @DependsOn("flyway")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(dataSource);

        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(environment.getRequiredProperty("mybatis.mapper-locations")));

        factoryBean.setTypeAliasesPackage(environment.getRequiredProperty("mybatis.type-aliases-package"));

        org.apache.ibatis.session.Configuration myBatisConfiguration = new org.apache.ibatis.session.Configuration();

        myBatisConfiguration.setMapUnderscoreToCamelCase(
                Boolean.parseBoolean(environment.getRequiredProperty("mybatis.map-underscore-to-camel-case")));

        myBatisConfiguration.setCacheEnabled(
                Boolean.parseBoolean(environment.getRequiredProperty("mybatis.cache-enabled")));

        /*
         * Oracle có thể gặp lỗi khi MyBatis truyền null
         * với JDBC type mặc định OTHER.
         */
        myBatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);

        factoryBean.setConfiguration(myBatisConfiguration);

        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {

        return new DataSourceTransactionManager(dataSource);
    }
}
