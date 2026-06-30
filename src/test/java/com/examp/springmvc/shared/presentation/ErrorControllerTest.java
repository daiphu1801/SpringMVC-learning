package com.examp.springmvc.shared.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ErrorControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ErrorController errorController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(errorController).build();
    }

    @Test
    @DisplayName("GET /error/400 - Should return error/400 view")
    void shouldReturn400View() throws Exception {
        mockMvc.perform(get("/error/400")).andExpect(status().isOk()).andExpect(view().name("error/400"));
    }

    @Test
    @DisplayName("GET /error/404 - Should return error/404 view")
    void shouldReturn404View() throws Exception {
        mockMvc.perform(get("/error/404")).andExpect(status().isOk()).andExpect(view().name("error/404"));
    }

    @Test
    @DisplayName("GET /error/500 - Should return error/500 view")
    void shouldReturn500View() throws Exception {
        mockMvc.perform(get("/error/500")).andExpect(status().isOk()).andExpect(view().name("error/500"));
    }
}
