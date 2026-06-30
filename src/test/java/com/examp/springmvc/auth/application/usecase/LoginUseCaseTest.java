package com.examp.springmvc.auth.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import com.examp.springmvc.user.infrastructure.persistence.UserDataAccessMapper;
import com.examp.springmvc.user.infrastructure.persistence.UserDbEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private PasswordHasher passwordHasher;

    private final UserDataAccessMapper userDataAccessMapper = new UserDataAccessMapper();

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(userPersistencePort, passwordHasher);
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
        User user = userDataAccessMapper.toDomain(entity);
        when(userPersistencePort.findByUsername("test_user")).thenReturn(Optional.of(user));
        when(passwordHasher.check("raw_password", "hashed_password")).thenReturn(true);

        com.examp.springmvc.auth.application.dto.AuthenticatedUserDTO result =
                loginUseCase.execute("test_user", "raw_password");

        assertNotNull(result);
        assertEquals("test_user", result.getUsername());
        verify(userPersistencePort).findByUsername("test_user");
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
        when(userPersistencePort.findByUsername("unknown")).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> loginUseCase.execute("unknown", "password"));
        assertEquals("Tài khoản hoặc mật khẩu không chính xác", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when account is inactive")
    void shouldThrowExceptionWhenAccountIsInactive() {
        UserDbEntity entity = testUserDbEntity();
        entity.setStatus("INACTIVE");
        User user = userDataAccessMapper.toDomain(entity);
        when(userPersistencePort.findByUsername("test_user")).thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> loginUseCase.execute("test_user", "password"));
        assertEquals("Tài khoản đang bị khóa", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when password does not match")
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        UserDbEntity entity = testUserDbEntity();
        User user = userDataAccessMapper.toDomain(entity);
        when(userPersistencePort.findByUsername("test_user")).thenReturn(Optional.of(user));
        when(passwordHasher.check("wrong_password", "hashed_password")).thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> loginUseCase.execute("test_user", "wrong_password"));
        assertEquals("Tài khoản hoặc mật khẩu không chính xác", exception.getMessage());
    }
}
