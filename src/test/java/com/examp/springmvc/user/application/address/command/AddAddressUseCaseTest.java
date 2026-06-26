package com.examp.springmvc.user.application.address.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.user.domain.model.Email;
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
class AddAddressUseCaseTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @InjectMocks
    private AddAddressUseCase addAddressUseCase;

    @Test
    @DisplayName("Should add address and save user successfully")
    void shouldAddAddressSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail(new Email("test@example.com"));
        user.setPhone("0987654321");

        AddAddressCommand command = new AddAddressCommand(
                1L, "Nguyễn Văn A", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 1", true);

        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(user));

        addAddressUseCase.execute(command);

        assertEquals(1, user.getAddresses().size());
        assertEquals("Nguyễn Văn A", user.getAddresses().get(0).getReceiverName());
        verify(userPersistencePort).save(user);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        AddAddressCommand command = new AddAddressCommand(
                99L, "Nguyễn Văn A", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 1", true);

        when(userPersistencePort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> addAddressUseCase.execute(command));
        verify(userPersistencePort, never()).save(any(User.class));
    }
}
