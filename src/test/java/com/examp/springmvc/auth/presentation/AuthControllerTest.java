package com.examp.springmvc.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.examp.springmvc.auth.application.ports.input.LoginInputPort;
import com.examp.springmvc.auth.application.ports.input.LogoutInputPort;
import com.examp.springmvc.auth.application.ports.input.RegisterCommand;
import com.examp.springmvc.auth.application.ports.input.RegisterInputPort;
import com.examp.springmvc.user.domain.model.Email;
import com.examp.springmvc.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoginInputPort loginInputPort;

    @Mock
    private LogoutInputPort logoutInputPort;

    @Mock
    private RegisterInputPort registerInputPort;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("GET /login - Should return login view")
    void shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("POST /login - Should login and redirect on success")
    void shouldLoginAndRedirect() throws Exception {
        User user = new User(
                "john",
                "John Doe",
                new Email("john@example.com"),
                "0987654321",
                null,
                com.examp.springmvc.user.domain.model.UserRole.USER);
        when(loginInputPort.execute("john", "pass")).thenReturn(user);

        mockMvc.perform(post("/login").param("username", "john").param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(loginInputPort).execute("john", "pass");
    }

    @Test
    @DisplayName("GET /register - Should return register view")
    void shouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("POST /register - Should register and redirect on success")
    void shouldRegisterAndRedirect() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "john")
                        .param("fullName", "John Doe")
                        .param("email", "john@example.com")
                        .param("phone", "0901234567")
                        .param("password", "Password123!"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("success"));

        verify(registerInputPort).execute(any(RegisterCommand.class));
    }

    @Test
    @DisplayName("POST /register - Should return register form with error on failure")
    void shouldReturnRegisterFormOnError() throws Exception {
        doThrow(new IllegalArgumentException("Username đã tồn tại"))
                .when(registerInputPort)
                .execute(any(RegisterCommand.class));

        mockMvc.perform(post("/register")
                        .param("username", "john")
                        .param("fullName", "John Doe")
                        .param("email", "john@example.com")
                        .param("phone", "0901234567")
                        .param("password", "Password123!"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attribute("error", "Username đã tồn tại"));
    }
}
