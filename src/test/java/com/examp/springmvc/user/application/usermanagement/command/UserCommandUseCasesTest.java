package com.examp.springmvc.user.application.usermanagement.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class UserCommandUseCasesTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

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
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        CreateUserCommand command = new CreateUserCommand(
                "user1", "Nguyen Van A", "user1@example.com", "0900000000", "password123", "USER");

        when(userPersistencePort.findByUsername("user1")).thenReturn(Optional.empty());
        when(passwordHasher.hash("password123")).thenReturn("hashed123");

        createUserUseCase.execute(command);

        verify(userPersistencePort).findByUsername("user1");
        verify(userPersistencePort).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username exists during creation")
    void shouldThrowExceptionWhenUsernameExists() {
        CreateUserCommand command = new CreateUserCommand(
                "user1", "Nguyen Van A", "user1@example.com", "0900000000", "password123", "USER");
        when(userPersistencePort.findByUsername("user1")).thenReturn(Optional.of(newUser(1L, "user1")));
        when(passwordHasher.hash("password123")).thenReturn("hashed123");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> createUserUseCase.execute(command));

        assertEquals("Username đã tồn tại", exception.getMessage());
        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        UpdateUserCommand command = new UpdateUserCommand(
                1L, "user1", "Nguyen Van A", "user1@example.com", "0900000000", "ACTIVE", "password123", "USER");
        User existing = newUser(1L, "user1");

        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordHasher.hash("password123")).thenReturn("hashed123");

        updateUserUseCase.execute(command);

        verify(userPersistencePort).findById(1L);
        verify(userPersistencePort).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when update ID is null")
    void shouldThrowExceptionWhenUpdateIdIsNull() {
        UpdateUserCommand command = new UpdateUserCommand(
                null, "user1", "Nguyen Van A", "user1@example.com", "0900000000", "ACTIVE", "password123", "USER");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> updateUserUseCase.execute(command));

        assertEquals("ID không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        User user = newUser(1L, "user1");
        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(user));

        deleteUserUseCase.execute(new DeleteUserCommand(1L));

        verify(userPersistencePort).findById(1L);
        verify(userPersistencePort).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when delete user not found")
    void shouldThrowExceptionWhenDeleteUserNotFound() {
        when(userPersistencePort.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> deleteUserUseCase.execute(new DeleteUserCommand(99L)));

        assertEquals("Không tìm thấy user với id: 99", exception.getMessage());
    }
}
