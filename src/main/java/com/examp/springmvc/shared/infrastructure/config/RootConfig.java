package com.examp.springmvc.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableAsync
@Import({MyBatisConfig.class, CloudinaryConfig.class, MailConfig.class})
@ComponentScan(
        basePackages = {
            "com.examp.springmvc.user.application",
            "com.examp.springmvc.user.infrastructure",
            "com.examp.springmvc.auth.application",
            "com.examp.springmvc.auth.infrastructure",
            "com.examp.springmvc.catalog.application",
            "com.examp.springmvc.catalog.infrastructure",
            "com.examp.springmvc.order.application",
            "com.examp.springmvc.order.infrastructure"
        })
public class RootConfig {

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("OrderEvent-");
        executor.initialize();
        return executor;
    }
}
