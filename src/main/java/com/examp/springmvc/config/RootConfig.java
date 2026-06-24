package com.examp.springmvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Import(MyBatisConfig.class)
@ComponentScan(basePackages = {
        "com.examp.springmvc.service"
})
public class RootConfig {
}