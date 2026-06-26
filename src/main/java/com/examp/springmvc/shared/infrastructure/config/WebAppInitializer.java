package com.examp.springmvc.shared.infrastructure.config;

import jakarta.servlet.Filter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {RootConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] {WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();

        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);

        return new Filter[] {encodingFilter};
    }

    @Override
    protected void customizeRegistration(jakarta.servlet.ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(new jakarta.servlet.MultipartConfigElement(
                "", // location (empty string means default/temp)
                10 * 1024 * 1024, // maxFileSize (10MB)
                20 * 1024 * 1024, // maxRequestSize (20MB)
                0 // fileSizeThreshold
                ));
    }
}
