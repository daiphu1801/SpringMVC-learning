package com.examp.springmvc.shared.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should intercept IllegalArgumentException and return 400 Bad Request status and error/400 view")
    void shouldHandleBadRequestExceptions() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/400"));
    }

    @Test
    @DisplayName("Should intercept unhandled exception and return 500 Internal Server Error status and error/500 view")
    void shouldHandleInternalServerErrors() throws Exception {
        mockMvc.perform(get("/test/internal-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error/500"));
    }

    @Controller
    static class TestController {
        @GetMapping("/test/bad-request")
        public void throwBadRequest() {
            throw new IllegalArgumentException("Invalid argument test");
        }

        @GetMapping("/test/internal-error")
        public void throwInternalError() {
            throw new NullPointerException("Null pointer test");
        }
    }
}
