package com.examp.springmvc.auth.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.auth.application.ports.input.RegisterCommand;
import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.user.domain.model.Email;
import com.examp.springmvc.user.domain.model.Password;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private RegisterUseCase registerUseCase;

    private User newUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setFullName("Nguyen Van A");
        user.setEmail(new Email(username + "@example.com"));
        user.setPhone("0900000000");
        user.setStatus("ACTIVE");
        user.setPassword(Password.fromHashed("password123"));
        user.setRole("USER");
        return user;
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        RegisterCommand command =
                new RegisterCommand("newuser", "Nguyen Van A", "newuser@example.com", "0900000000", "password123");

        when(userPersistencePort.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordHasher.hash("password123")).thenReturn("hashed123");

        registerUseCase.execute(command);

        verify(userPersistencePort).findByUsername("newuser");
        verify(passwordHasher).hash("password123");
        verify(userPersistencePort).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        RegisterCommand command =
                new RegisterCommand("newuser", "Nguyen Van A", "newuser@example.com", "0900000000", "password123");
        when(userPersistencePort.findByUsername("newuser")).thenReturn(Optional.of(newUser(1L, "newuser")));
        when(passwordHasher.hash("password123")).thenReturn("hashed123");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> registerUseCase.execute(command));

        assertEquals("Username đã tồn tại", exception.getMessage());
        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when command is null")
    void shouldThrowExceptionWhenCommandIsNull() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> registerUseCase.execute(null));

        assertEquals("Dữ liệu đăng ký không được null", exception.getMessage());
    }
}
