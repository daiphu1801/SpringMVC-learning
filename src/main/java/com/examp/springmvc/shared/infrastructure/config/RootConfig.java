package com.examp.springmvc.shared.infrastructure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Import(MyBatisConfig.class)
@ComponentScan(
        basePackages = {
            "com.examp.springmvc.user.application.usecase",
            "com.examp.springmvc.user.infrastructure.persistence.mybatis"
        })
public class RootConfig {}
