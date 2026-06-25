package com.examp.springmvc.user.application.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.user.infrastructure.mapper.UserQueryMapper;
import com.examp.springmvc.user.infrastructure.persistence.UserDbEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserQueryUseCasesTest {

    @Mock
    private UserQueryMapper userQueryMapper;

    @InjectMocks
    private FindAllUsersUseCase findAllUsersUseCase;

    @InjectMocks
    private FindUserByIdUseCase findUserByIdUseCase;

    private UserDbEntity testUserDbEntity(Long id, String username) {
        return new UserDbEntity(
                id,
                username,
                "Nguyen Van A",
                username + "@example.com",
                "0900000000",
                "ACTIVE",
                "password123",
                "USER",
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() {
        List<UserDbEntity> entities = List.of(testUserDbEntity(1L, "user1"), testUserDbEntity(2L, "user2"));
        when(userQueryMapper.findAll()).thenReturn(entities);

        List<UserDTO> result = findAllUsersUseCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        verify(userQueryMapper).findAll();
    }

    @Test
    @DisplayName("Should return user by ID when found")
    void shouldReturnUserByIdWhenFound() {
        UserDbEntity entity = testUserDbEntity(1L, "user1");
        when(userQueryMapper.findById(1L)).thenReturn(entity);

        UserDTO result = findUserByIdUseCase.execute(1L);

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        verify(userQueryMapper).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userQueryMapper.findById(99L)).thenReturn(null);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> findUserByIdUseCase.execute(99L));

        assertEquals("Không tìm thấy user với id: 99", exception.getMessage());
    }
}
