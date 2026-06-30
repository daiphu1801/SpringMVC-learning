package com.examp.springmvc.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserAddressTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                "user1",
                "Nguyen Van A",
                new Email("user1@example.com"),
                "0987654321",
                null,
                com.examp.springmvc.user.domain.model.UserRole.USER);
        user.assignId(1L);
    }

    @Test
    @DisplayName("Should set first address to default automatically")
    void shouldSetFirstAddressToDefault() {
        Address address =
                new Address(null, "Nguyễn Văn A", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 1", false);

        user.addAddress(address);

        assertEquals(1, user.getAddresses().size());
        assertTrue(user.getAddresses().get(0).isDefault());
    }

    @Test
    @DisplayName("Should maintain at most 5 addresses and throw exception on 6th")
    void shouldMaintainAtMostFiveAddresses() {
        for (int i = 1; i <= 5; i++) {
            user.addAddress(new Address(
                    (long) i, "Receiver " + i, "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số " + i, false));
        }

        assertEquals(5, user.getAddresses().size());

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.addAddress(
                        new Address(6L, "Receiver 6", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 6", false)));
        assertEquals("Không thể thêm quá 5 địa chỉ", exception.getMessage());
    }

    @Test
    @DisplayName("Should clear other defaults when adding a new default address")
    void shouldClearOtherDefaults() {
        Address first = new Address(1L, "Receiver 1", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 1", true);
        Address second = new Address(2L, "Receiver 2", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 2", true);

        user.addAddress(first);
        assertTrue(user.getAddresses().get(0).isDefault());

        user.addAddress(second);
        assertEquals(2, user.getAddresses().size());
        assertFalse(user.getAddresses().get(0).isDefault());
        assertTrue(user.getAddresses().get(1).isDefault());
    }

    @Test
    @DisplayName("Should assign another default address when current default is removed")
    void shouldAssignNewDefaultWhenOldRemoved() {
        Address first = new Address(1L, "Receiver 1", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 1", true);
        Address second = new Address(2L, "Receiver 2", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 2", false);

        user.addAddress(first);
        user.addAddress(second);

        assertTrue(user.getAddresses().get(0).isDefault());
        assertFalse(user.getAddresses().get(1).isDefault());

        user.removeAddress(1L);

        assertEquals(1, user.getAddresses().size());
        assertTrue(user.getAddresses().get(0).isDefault());
    }
}
