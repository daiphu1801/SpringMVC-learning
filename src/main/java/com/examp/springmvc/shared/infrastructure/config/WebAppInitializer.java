package com.examp.springmvc.shared.infrastructure.config;

import com.examp.springmvc.shared.infrastructure.security.SecurityHeadersFilter;
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

        // --- Load Config Properties Manually Before Spring Context Initializes ---
        java.util.Properties props = new java.util.Properties();
        try (java.io.InputStream is =
                WebAppInitializer.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (java.io.IOException e) {
            // fallback
        }
        boolean isCookieSecure = Boolean.parseBoolean(props.getProperty("app.cookie.secure", "false"));
        int sessionTimeoutMinutes = Integer.parseInt(props.getProperty("app.session.timeout-minutes", "30"));

        // --- Session Cookie Security ---
        // HttpOnly: prevents JavaScript (e.g. XSS) from reading the session cookie.
        // Secure: must be set to true only in production behind HTTPS.
        // SameSite=Lax: browsers only send the cookie for same-site requests and
        //               top-level GET navigations from other sites, blocking CSRF
        //               via cross-origin form POSTs without breaking normal links.
        SessionCookieConfig cookieConfig = servletContext.getSessionCookieConfig();
        cookieConfig.setHttpOnly(true);
        cookieConfig.setSecure(isCookieSecure);
        cookieConfig.setAttribute("SameSite", "Lax");

        // --- Session Timeout Config ---
        servletContext.setSessionTimeout(sessionTimeoutMinutes);

        // Register SecurityHeadersFilter globally
        jakarta.servlet.FilterRegistration.Dynamic securityFilter =
                servletContext.addFilter("securityHeadersFilter", new SecurityHeadersFilter());
        securityFilter.addMappingForUrlPatterns(null, true, "/*");
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
