package com.examp.springmvc.shared.infrastructure.security;

import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecurityHeadersFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityHeadersFilter securityHeadersFilter;

    @Test
    @DisplayName("Should set security headers on response and invoke filter chain")
    void shouldSetSecurityHeadersAndDoFilter() throws Exception {
        securityHeadersFilter.doFilter(request, response, filterChain);

        // Verify all security headers are set
        verify(response)
                .setHeader(
                        "Content-Security-Policy",
                        "default-src 'self'; "
                                + "script-src 'self'; "
                                + "style-src 'self' https://fonts.googleapis.com; "
                                + "font-src 'self' https://fonts.gstatic.com; "
                                + "img-src 'self' data: https://img.vietqr.io https://res.cloudinary.com; "
                                + "frame-ancestors 'self'; "
                                + "form-action 'self'; "
                                + "base-uri 'self'; "
                                + "object-src 'none';");
        verify(response).setHeader("X-Frame-Options", "SAMEORIGIN");
        verify(response).setHeader("X-Content-Type-Options", "nosniff");
        verify(response).setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        verify(response).setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        verify(response).setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");

        // Verify filter chain is invoked
        verify(filterChain).doFilter(request, response);
    }
}
