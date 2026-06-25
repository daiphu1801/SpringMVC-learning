package com.examp.springmvc.auth.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.infrastructure.mapper.UserQueryMapper;
import com.examp.springmvc.user.infrastructure.persistence.UserDataAccessMapper;
import com.examp.springmvc.user.infrastructure.persistence.UserDbEntity;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserQueryMapper userQueryMapper;

    @Mock
    private PasswordHasher passwordHasher;

    private final UserDataAccessMapper userDataAccessMapper = new UserDataAccessMapper();

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(userQueryMapper, passwordHasher, userDataAccessMapper);
    }

    private UserDbEntity testUserDbEntity() {
        return new UserDbEntity(
                1L,
                "test_user",
                "Full Name",
                "email@test.com",
                "123456",
                "ACTIVE",
                "hashed_password",
                "USER",
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void shouldLoginSuccessfully() {
        UserDbEntity entity = testUserDbEntity();
        when(userQueryMapper.findByUsername("test_user")).thenReturn(entity);
        when(passwordHasher.check("raw_password", "hashed_password")).thenReturn(true);

        User result = loginUseCase.execute("test_user", "raw_password");

        assertNotNull(result);
        assertEquals("test_user", result.getUsername());
        verify(userQueryMapper).findByUsername("test_user");
        verify(passwordHasher).check("raw_password", "hashed_password");
    }

    @Test
    @DisplayName("Should throw exception when username is empty")
    void shouldThrowExceptionWhenUsernameIsEmpty() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> loginUseCase.execute("", "password"));
        assertEquals("Username không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when password is empty")
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> loginUseCase.execute("username", " "));
        assertEquals("Mật khẩu không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userQueryMapper.findByUsername("unknown")).thenReturn(null);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> loginUseCase.execute("unknown", "password"));
        assertEquals("Tài khoản hoặc mật khẩu không chính xác", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when account is inactive")
    void shouldThrowExceptionWhenAccountIsInactive() {
        UserDbEntity entity = testUserDbEntity();
        entity.setStatus("INACTIVE");
        when(userQueryMapper.findByUsername("test_user")).thenReturn(entity);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> loginUseCase.execute("test_user", "password"));
        assertEquals("Tài khoản đang bị khóa", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when password does not match")
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        UserDbEntity entity = testUserDbEntity();
        when(userQueryMapper.findByUsername("test_user")).thenReturn(entity);
        when(passwordHasher.check("wrong_password", "hashed_password")).thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> loginUseCase.execute("test_user", "wrong_password"));
        assertEquals("Tài khoản hoặc mật khẩu không chính xác", exception.getMessage());
    }
}
