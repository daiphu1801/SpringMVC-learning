package com.examp.springmvc.shared.infrastructure.config;

import jakarta.servlet.Filter;
import jakarta.servlet.SessionCookieConfig;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    public void onStartup(jakarta.servlet.ServletContext servletContext) throws jakarta.servlet.ServletException {
        super.onStartup(servletContext);

        // App version based on system startup time to force cache refresh on deployment
        servletContext.setAttribute("appVersion", String.valueOf(System.currentTimeMillis()));

        // --- Session Cookie Security ---
        // HttpOnly: prevents JavaScript (e.g. XSS) from reading the session cookie.
        // Secure: must be set to true only in production behind HTTPS;
        //         set false here to allow development on plain HTTP localhost.
        // SameSite=Lax: browsers only send the cookie for same-site requests and
        //               top-level GET navigations from other sites, blocking CSRF
        //               via cross-origin form POSTs without breaking normal links.
        SessionCookieConfig cookieConfig = servletContext.getSessionCookieConfig();
        cookieConfig.setHttpOnly(true);
        cookieConfig.setSecure(false); // TODO: set true in production (HTTPS only)
        cookieConfig.setAttribute("SameSite", "Lax");
    }

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
