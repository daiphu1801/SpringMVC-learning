package com.examp.springmvc.shared.infrastructure.config;

import com.examp.springmvc.auth.infrastructure.security.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@PropertySource("classpath:application.properties")
@ComponentScan(
        basePackages = {
            "com.examp.springmvc.user.presentation",
            "com.examp.springmvc.auth.presentation",
            "com.examp.springmvc.auth.infrastructure.security"
        })
public class WebConfig implements WebMvcConfigurer {

    private final Environment environment;
    private final SecurityInterceptor securityInterceptor;

    @Autowired
    public WebConfig(Environment environment, SecurityInterceptor securityInterceptor) {
        this.environment = environment;
        this.securityInterceptor = securityInterceptor;
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();

        resolver.setPrefix(environment.getRequiredProperty("spring.mvc.view.prefix"));

        resolver.setSuffix(environment.getRequiredProperty("spring.mvc.view.suffix"));

        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

        configurer.enable();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor);
    }
}
