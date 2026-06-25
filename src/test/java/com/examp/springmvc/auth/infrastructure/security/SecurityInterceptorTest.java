package com.examp.springmvc.auth.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.user.domain.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecurityInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private SecurityInterceptor securityInterceptor;

    private User testUser(String role) {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setRole(role);
        return user;
    }

    @BeforeEach
    void setUp() {
        when(request.getContextPath()).thenReturn("/demo");
    }

    @Test
    @DisplayName("Should allow public endpoints and resources without login")
    void shouldAllowPublicEndpoints() throws Exception {
        when(request.getRequestURI()).thenReturn("/demo/login");
        assertTrue(securityInterceptor.preHandle(request, response, null));

        when(request.getRequestURI()).thenReturn("/demo/resources/css/style.css");
        assertTrue(securityInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Should redirect unauthenticated users to login page")
    void shouldRedirectUnauthenticated() throws Exception {
        when(request.getRequestURI()).thenReturn("/demo/users");
        when(request.getSession(false)).thenReturn(null);

        assertFalse(securityInterceptor.preHandle(request, response, null));
        verify(response).sendRedirect("/demo/login");
    }

    @Test
    @DisplayName("Should allow logged-in user to access user list")
    void shouldAllowUserListAccess() throws Exception {
        when(request.getRequestURI()).thenReturn("/demo/users");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(testUser("USER"));

        assertTrue(securityInterceptor.preHandle(request, response, null));
    }

    @Test
    @DisplayName("Should deny non-admin access to create user form")
    void shouldDenyNonAdminToWriteActions() throws Exception {
        when(request.getRequestURI()).thenReturn("/demo/users/create");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(testUser("USER"));

        assertFalse(securityInterceptor.preHandle(request, response, null));
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
    }

    @Test
    @DisplayName("Should allow admin access to create user form")
    void shouldAllowAdminToWriteActions() throws Exception {
        when(request.getRequestURI()).thenReturn("/demo/users/create");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(testUser("ADMIN"));

        assertTrue(securityInterceptor.preHandle(request, response, null));
    }
}
