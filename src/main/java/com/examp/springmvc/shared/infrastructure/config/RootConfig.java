package com.examp.springmvc.shared.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Import(MyBatisConfig.class)
@ComponentScan(
        basePackages = {
            "com.examp.springmvc.user.application",
            "com.examp.springmvc.user.infrastructure",
            "com.examp.springmvc.auth.application",
            "com.examp.springmvc.auth.infrastructure"
        })
public class RootConfig {}
