package com.examp.springmvc.auth.infrastructure.security;

import com.examp.springmvc.user.domain.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Set security headers
        response.setHeader(
                "Content-Security-Policy",
                "default-src 'self'; "
                        + "script-src 'self'; "
                        + "style-src 'self' https://fonts.googleapis.com; "
                        + "font-src 'self' https://fonts.gstatic.com; "
                        + "img-src 'self' data: https://img.vietqr.io https://res.cloudinary.com; "
                        + "frame-ancestors 'self';");
        // Prevent embedding in iframes from other origins (clickjacking)
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        // Prevent browsers from guessing the content type (MIME-sniffing)
        response.setHeader("X-Content-Type-Options", "nosniff");
        // Force HTTPS for 1 year; includeSubDomains extends to all subdomains.
        // Only effective when served over HTTPS (ignored on HTTP/localhost).
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        // Controls how much referrer information the browser sends.
        // strict-origin-when-cross-origin: sends full URL only for same-origin,
        // and only the origin (no path) for cross-origin HTTPS requests.
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        // Restrict access to browser features this app does not use.
        response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // 1. Bypass static resources completely
        if (path.startsWith("/resources/")) {
            return true;
        }

        // 2. Initialize or retrieve CSRF token in session
        HttpSession session = request.getSession(true);
        String csrfToken = (String) session.getAttribute("CSRF_TOKEN");
        if (csrfToken == null) {
            csrfToken = UUID.randomUUID().toString();
            session.setAttribute("CSRF_TOKEN", csrfToken);
        }
        request.setAttribute("csrfToken", csrfToken);

        // 3. Validate CSRF token for POST requests
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String requestToken = request.getParameter("csrfToken");
            if (requestToken == null || !requestToken.equals(csrfToken)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF Token.");
                return false;
            }
        }

        // Allow public endpoints
        if (path.equals("/login")
                || path.equals("/logout")
                || path.equals("/register")
                || path.equals("/architecture")) {
            return true;
        }

        // Authentication check
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // Authorization checks
        if (path.startsWith("/users")) {
            boolean isWriteAction = path.equals("/users/create")
                    || path.startsWith("/users/edit")
                    || path.startsWith("/users/delete")
                    || (path.equals("/users") && "POST".equalsIgnoreCase(request.getMethod()))
                    || (path.matches("/users/\\d+") && "POST".equalsIgnoreCase(request.getMethod()));

            if (isWriteAction && !currentUser.isAdmin()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
                return false;
            }
        }

        if (path.startsWith("/admin") && !currentUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang quản trị.");
            return false;
        }

        return true;
    }
}
