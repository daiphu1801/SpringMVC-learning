package com.examp.springmvc.user.application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.user.domain.model.Address;
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
class RemoveAddressUseCaseTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @InjectMocks
    private RemoveAddressUseCase removeAddressUseCase;

    @Test
    @DisplayName("Should remove address and save user successfully")
    void shouldRemoveAddressSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail(new Email("test@example.com"));
        user.setPhone("0987654321");

        Address address =
                new Address(10L, "Nguyễn Văn A", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 1", true);
        user.addAddress(address);

        assertEquals(1, user.getAddresses().size());

        when(userPersistencePort.findById(1L)).thenReturn(Optional.of(user));

        removeAddressUseCase.execute(1L, 10L);

        assertEquals(0, user.getAddresses().size());
        verify(userPersistencePort).save(user);
    }
}
