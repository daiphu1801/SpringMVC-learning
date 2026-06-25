package com.examp.springmvc.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.examp.springmvc.user.application.command.CreateUserCommand;
import com.examp.springmvc.user.application.command.CreateUserInputPort;
import com.examp.springmvc.user.application.command.DeleteUserCommand;
import com.examp.springmvc.user.application.command.DeleteUserInputPort;
import com.examp.springmvc.user.application.command.UpdateUserCommand;
import com.examp.springmvc.user.application.command.UpdateUserInputPort;
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
class UserCommandControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateUserInputPort createUserInputPort;

    @Mock
    private UpdateUserInputPort updateUserInputPort;

    @Mock
    private DeleteUserInputPort deleteUserInputPort;

    @InjectMocks
    private UserCommandController userCommandController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userCommandController).build();
    }

    @Test
    @DisplayName("POST /users - Should create user and redirect")
    void shouldCreateUserAndRedirect() throws Exception {
        mockMvc.perform(post("/users")
                        .param("username", "john_doe")
                        .param("fullName", "John Doe")
                        .param("email", "john@example.com")
                        .param("phone", "0901234567")
                        .param("password", "password123")
                        .param("role", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(createUserInputPort).execute(any(CreateUserCommand.class));
    }

    @Test
    @DisplayName("POST /users/{id} - Should update user and redirect")
    void shouldUpdateUserAndRedirect() throws Exception {
        mockMvc.perform(post("/users/1")
                        .param("username", "john_doe")
                        .param("fullName", "John Doe")
                        .param("email", "john@example.com")
                        .param("phone", "0901234567")
                        .param("status", "ACTIVE")
                        .param("role", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(updateUserInputPort).execute(any(UpdateUserCommand.class));
    }

    @Test
    @DisplayName("POST /users/delete/{id} - Should delete user and redirect")
    void shouldDeleteUserAndRedirect() throws Exception {
        mockMvc.perform(post("/users/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(deleteUserInputPort).execute(any(DeleteUserCommand.class));
    }
}
