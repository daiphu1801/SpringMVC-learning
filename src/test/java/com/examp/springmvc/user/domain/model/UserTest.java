package com.examp.springmvc.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    private User createValidUser() {
        User user =
                new User("john_doe", "John Doe", new Email("john.doe@example.com"), "0901234567", null, UserRole.USER);
        user.assignId(1L);
        return user;
    }

    @Test
    @DisplayName("Should validate successfully with valid fields")
    void shouldValidateSuccessfully() {
        User user = createValidUser();
        assertDoesNotThrow(user::validate);
    }

    @Test
    @DisplayName("Should throw exception when username is empty in constructor")
    void shouldThrowExceptionWhenUsernameIsEmpty() {
        Email email = new Email("john.doe@example.com");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("", "John Doe", email, "0901234567", null, UserRole.USER));
        assertEquals("Username không được để trống", exception.getMessage());

        exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(null, "John Doe", email, "0901234567", null, UserRole.USER));
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
    @DisplayName("Should throw exception when fullName is empty in constructor")
    void shouldThrowExceptionWhenFullNameIsEmpty() {
        Email email = new Email("john.doe@example.com");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("john_doe", "", email, "0901234567", null, UserRole.USER));
        assertEquals("Họ tên không được để trống", exception.getMessage());

        exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("john_doe", null, email, "0901234567", null, UserRole.USER));
        assertEquals("Họ tên không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating profile with empty name")
    void shouldThrowExceptionWhenUpdatingProfileWithEmptyName() {
        User user = createValidUser();
        Email email = new Email("john.doe@example.com");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> user.updateProfile("", "0901234567", email));
        assertEquals("Họ tên không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when role is invalid")
    void shouldThrowExceptionWhenRoleIsInvalid() {
        User user = createValidUser();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> user.changeRole(null));
        assertEquals("Vai trò không hợp lệ", exception.getMessage());
    }

    @Test
    @DisplayName("Should detect admin role correctly")
    void shouldDetectAdminRole() {
        User user = createValidUser();
        user.changeRole(UserRole.USER);
        assertEquals(false, user.isAdmin());

        user.changeRole(UserRole.ADMIN);
        assertEquals(true, user.isAdmin());
    }

    @Test
    @DisplayName("Should activate user status")
    void shouldActivateUser() {
        User user = createValidUser();
        user.changeStatus(UserStatus.INACTIVE);
        user.activate();
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Should deactivate user status")
    void shouldDeactivateUser() {
        User user = createValidUser();
        user.changeStatus(UserStatus.ACTIVE);
        user.deactivate();
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Should protect ID assignment from being reassigned")
    void shouldProtectIdAssignment() {
        User user = createValidUser();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> user.assignId(2L));
        assertEquals("ID đã được gán và không thể thay đổi", exception.getMessage());
    }
}
