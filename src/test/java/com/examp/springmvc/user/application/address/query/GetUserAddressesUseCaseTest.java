package com.examp.springmvc.user.application.address.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserAddressesUseCaseTest {

    @Mock
    private UserAddressQueryPort userAddressQueryPort;

    @InjectMocks
    private GetUserAddressesUseCase getUserAddressesUseCase;

    @Test
    @DisplayName("Should return addresses when user ID is valid")
    void shouldReturnAddressesWhenUserIdIsValid() {
        AddressDTO addr1 =
                new AddressDTO(1L, "Receiver 1", "0901234567", "Province", "District", "Ward", "Street", true);
        AddressDTO addr2 =
                new AddressDTO(2L, "Receiver 2", "0907654321", "Province", "District", "Ward", "Street", false);
        when(userAddressQueryPort.findByUserId(1L)).thenReturn(List.of(addr1, addr2));

        List<AddressDTO> result = getUserAddressesUseCase.execute(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Receiver 1", result.get(0).getReceiverName());
        verify(userAddressQueryPort).findByUserId(1L);
    }

    @Test
    @DisplayName("Should throw exception when user ID is null")
    void shouldThrowExceptionWhenUserIdIsNull() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> getUserAddressesUseCase.execute(null));

        assertEquals("User ID không được null", exception.getMessage());
    }
}
