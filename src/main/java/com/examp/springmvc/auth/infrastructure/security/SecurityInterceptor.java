package com.examp.springmvc.auth.infrastructure.security;

import com.examp.springmvc.auth.application.dto.AuthenticatedUserDTO;
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
        AuthenticatedUserDTO currentUser = (AuthenticatedUserDTO) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // Authorization checks
        if (path.startsWith("/users") && !path.startsWith("/users/addresses")) {
            if (!currentUser.isAdmin()) {
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
