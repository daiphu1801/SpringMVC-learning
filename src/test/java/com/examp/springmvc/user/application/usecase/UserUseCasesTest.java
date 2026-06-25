package com.examp.springmvc.user.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.user.application.ports.output.UserPersistencePort;
import com.examp.springmvc.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserUseCasesTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @InjectMocks
    private FindAllUsersUseCase findAllUsersUseCase;

    @InjectMocks
    private FindUserByIdUseCase findUserByIdUseCase;

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
        user.setEmail(username + "@example.com");
        user.setPhone("0900000000");
        user.setStatus("ACTIVE");
        return user;
    }

    @Nested
    @DisplayName("FindAllUsersUseCase Tests")
    class FindAllUsersTests {
        @Test
        void shouldReturnAllUsers() {
            List<User> users = List.of(newUser(1L, "user1"), newUser(2L, "user2"));
            when(userPersistencePort.findAll()).thenReturn(users);

            List<User> result = findAllUsersUseCase.execute();

            assertSame(users, result);
            verify(userPersistencePort).findAll();
        }
    }

    @Nested
    @DisplayName("FindUserByIdUseCase Tests")
    class FindUserByIdTests {
        @Test
        void shouldReturnUserWhenFound() {
            User user = newUser(1L, "user1");
            when(userPersistencePort.findById(1L)).thenReturn(Optional.of(user));

            User result = findUserByIdUseCase.execute(1L);

            assertSame(user, result);
            verify(userPersistencePort).findById(1L);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userPersistencePort.findById(99L)).thenReturn(Optional.empty());

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> findUserByIdUseCase.execute(99L));

            assertEquals("Không tìm thấy user với id: 99", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("CreateUserUseCase Tests")
    class CreateUserTests {
        @Test
        void shouldCreateUserSuccessfully() {
            User user = newUser(null, "user1");
            user.setStatus(" ");

            when(userPersistencePort.findByUsername("user1")).thenReturn(Optional.empty());

            createUserUseCase.execute(user);

            assertEquals("ACTIVE", user.getStatus());
            verify(userPersistencePort).findByUsername("user1");
            verify(userPersistencePort).save(user);
        }

        @Test
        void shouldThrowExceptionWhenUserIsNull() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> createUserUseCase.execute(null));
            assertEquals("User không được null", exception.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenUsernameExists() {
            User user = newUser(null, "user1");
            when(userPersistencePort.findByUsername("user1")).thenReturn(Optional.of(newUser(1L, "user1")));

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> createUserUseCase.execute(user));

            assertEquals("Username đã tồn tại", exception.getMessage());
            verify(userPersistencePort).findByUsername("user1");
            verify(userPersistencePort, never()).save(user);
        }
    }

    @Nested
    @DisplayName("UpdateUserUseCase Tests")
    class UpdateUserTests {
        @Test
        void shouldUpdateUserSuccessfully() {
            User user = newUser(1L, "user1");
            when(userPersistencePort.findById(1L)).thenReturn(Optional.of(user));

            updateUserUseCase.execute(user);

            verify(userPersistencePort).findById(1L);
            verify(userPersistencePort).save(user);
        }

        @Test
        void shouldThrowExceptionWhenIdIsNull() {
            User user = newUser(null, "user1");

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> updateUserUseCase.execute(user));

            assertEquals("ID không được để trống", exception.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            User user = newUser(1L, "user1");
            when(userPersistencePort.findById(1L)).thenReturn(Optional.empty());

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> updateUserUseCase.execute(user));

            assertEquals("Không tìm thấy user với id: 1", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("DeleteUserUseCase Tests")
    class DeleteUserTests {
        @Test
        void shouldDeleteUserSuccessfully() {
            User user = newUser(1L, "user1");
            when(userPersistencePort.findById(1L)).thenReturn(Optional.of(user));

            deleteUserUseCase.execute(1L);

            verify(userPersistencePort).findById(1L);
            verify(userPersistencePort).deleteById(1L);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {
            when(userPersistencePort.findById(99L)).thenReturn(Optional.empty());

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> deleteUserUseCase.execute(99L));

            assertEquals("Không tìm thấy user với id: 99", exception.getMessage());
            verify(userPersistencePort, never()).deleteById(99L);
        }
    }
}
