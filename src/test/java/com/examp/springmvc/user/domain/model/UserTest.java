package com.examp.springmvc.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    private User createValidUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setFullName("John Doe");
        user.setEmail(new Email("john.doe@example.com"));
        user.setPhone("0901234567");
        user.setStatus("ACTIVE");
        return user;
    }

    @Test
    @DisplayName("Should validate successfully with valid fields")
    void shouldValidateSuccessfully() {
        User user = createValidUser();
        assertDoesNotThrow(user::validate);
    }

    @Test
    @DisplayName("Should throw exception when username is empty")
    void shouldThrowExceptionWhenUsernameIsEmpty() {
        User user = createValidUser();
        user.setUsername("");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Username không được để trống", exception.getMessage());

        user.setUsername(null);
        exception = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Username không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when email is empty")
    void shouldThrowExceptionWhenEmailIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Email(" "));
        assertEquals("Email không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when email format is invalid")
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
        assertEquals("Định dạng email không hợp lệ", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when fullName is empty")
    void shouldThrowExceptionWhenFullNameIsEmpty() {
        User user = createValidUser();
        user.setFullName("");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Họ tên không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when role is invalid")
    void shouldThrowExceptionWhenRoleIsInvalid() {
        User user = createValidUser();
        user.setRole("SUPER_ADMIN");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, user::validate);
        assertEquals("Vai trò không hợp lệ", exception.getMessage());
    }

    @Test
    @DisplayName("Should detect admin role correctly")
    void shouldDetectAdminRole() {
        User user = createValidUser();
        user.setRole("USER");
        assertEquals(false, user.isAdmin());

        user.setRole("ADMIN");
        assertEquals(true, user.isAdmin());
    }

    @Test
    @DisplayName("Should activate user status")
    void shouldActivateUser() {
        User user = createValidUser();
        user.setStatus("INACTIVE");
        user.activate();
        assertEquals("ACTIVE", user.getStatus());
    }

    @Test
    @DisplayName("Should deactivate user status")
    void shouldDeactivateUser() {
        User user = createValidUser();
        user.setStatus("ACTIVE");
        user.deactivate();
        assertEquals("INACTIVE", user.getStatus());
    }
}
