package com.examp.springmvc.shared.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan({
    "com.examp.springmvc.user.infrastructure.mapper",
    "com.examp.springmvc.catalog.infrastructure.mapper",
    "com.examp.springmvc.order.infrastructure.mapper",
    "com.examp.springmvc.shared.infrastructure.task"
})
public class MyBatisIntegrationTestConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:testdb;MODE=Oracle;"
                + "DB_CLOSE_DELAY=-1;"
                + "INIT=RUNSCRIPT FROM 'classpath:test-schema.sql'");
        config.setUsername("sa");
        config.setPassword("");
        config.setPoolName("TestH2Pool");
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(5);
        config.setInitializationFailTimeout(1);
        return new HikariDataSource(config);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml"));

        org.apache.ibatis.session.Configuration myBatisConfiguration = new org.apache.ibatis.session.Configuration();
        myBatisConfiguration.setMapUnderscoreToCamelCase(true);
        myBatisConfiguration.setCacheEnabled(false);
        myBatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);

        factoryBean.setConfiguration(myBatisConfiguration);
        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
