package com.examp.springmvc.auth.infrastructure.security;

import com.examp.springmvc.user.domain.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Allow static resources and auth endpoints
        if (path.startsWith("/resources/")
                || path.equals("/login")
                || path.equals("/logout")
                || path.equals("/register")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

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

        return true;
    }
}
