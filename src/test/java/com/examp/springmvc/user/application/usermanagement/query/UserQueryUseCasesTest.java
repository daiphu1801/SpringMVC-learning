package com.examp.springmvc.user.application.usermanagement.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserQueryUseCasesTest {

    @Mock
    private UserQueryPort userQueryPort;

    @InjectMocks
    private FindAllUsersUseCase findAllUsersUseCase;

    @InjectMocks
    private FindUserByIdUseCase findUserByIdUseCase;

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

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() {
        List<UserDTO> dtos = List.of(testUserDTO(1L, "user1"), testUserDTO(2L, "user2"));
        when(userQueryPort.findAll()).thenReturn(dtos);

        List<UserDTO> result = findAllUsersUseCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        verify(userQueryPort).findAll();
    }

    @Test
    @DisplayName("Should return user by ID when found")
    void shouldReturnUserByIdWhenFound() {
        UserDTO dto = testUserDTO(1L, "user1");
        when(userQueryPort.findById(1L)).thenReturn(Optional.of(dto));

        UserDTO result = findUserByIdUseCase.execute(1L);

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        verify(userQueryPort).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userQueryPort.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> findUserByIdUseCase.execute(99L));

        assertEquals("Không tìm thấy user với id: 99", exception.getMessage());
    }
}
