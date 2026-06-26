package com.examp.springmvc.user.presentation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.examp.springmvc.user.application.usermanagement.query.FindAllUsersInputPort;
import com.examp.springmvc.user.application.usermanagement.query.FindUserByIdInputPort;
import com.examp.springmvc.user.application.usermanagement.query.UserDTO;
import java.time.LocalDateTime;
import java.util.List;
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
class UserQueryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FindAllUsersInputPort findAllUsersInputPort;

    @Mock
    private FindUserByIdInputPort findUserByIdInputPort;

    @InjectMocks
    private UserQueryController userQueryController;

    private UserDTO testUserDTO(Long id, String username) {
        return new UserDTO(
                id,
                username,
                "Nguyen Van A",
                username + "@example.com",
                "0900000000",
                "ACTIVE",
                "USER",
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userQueryController).build();
    }

    @Test
    @DisplayName("GET /users - Should return list view with users")
    void shouldReturnListView() throws Exception {
        List<UserDTO> users = List.of(testUserDTO(1L, "user1"), testUserDTO(2L, "user2"));
        when(findAllUsersInputPort.execute()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attribute("users", users));

        verify(findAllUsersInputPort).execute();
    }

    @Test
    @DisplayName("GET /users/create - Should return form view with new user template")
    void shouldReturnCreateFormView() throws Exception {
        mockMvc.perform(get("/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("GET /users/edit/{id} - Should return form view with existing user DTO")
    void shouldReturnEditFormView() throws Exception {
        UserDTO user = testUserDTO(1L, "user1");
        when(findUserByIdInputPort.execute(1L)).thenReturn(user);

        mockMvc.perform(get("/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"))
                .andExpect(model().attribute("user", user));

        verify(findUserByIdInputPort).execute(1L);
    }
}
