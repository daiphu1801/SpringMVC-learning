package com.examp.springmvc.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    @DisplayName("Should create address successfully with valid data")
    void shouldCreateAddressSuccessfully() {
        Address address =
                new Address(1L, "Nguyễn Văn A", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 12 Ngõ 34", true);

        assertEquals(1L, address.getId());
        assertEquals("Nguyễn Văn A", address.getReceiverName());
        assertEquals("0987654321", address.getReceiverPhone());
        assertEquals("Hà Nội", address.getProvince());
        assertEquals("Cầu Giấy", address.getDistrict());
        assertEquals("Dịch Vọng", address.getWard());
        assertEquals("Số 12 Ngõ 34", address.getStreetDetail());
        assertTrue(address.isDefault());
    }

    @Test
    @DisplayName("Should throw exception when receiver name is empty")
    void shouldThrowExceptionWhenReceiverNameIsEmpty() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Address(1L, "", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 12 Ngõ 34", true));
        assertEquals("Tên người nhận không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when receiver phone is empty")
    void shouldThrowExceptionWhenReceiverPhoneIsEmpty() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Address(1L, "Nguyễn Văn A", "  ", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 12 Ngõ 34", true));
        assertEquals("Số điện thoại không được để trống", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when phone is invalid length")
    void shouldThrowExceptionWhenPhoneIsInvalidLength() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Address(
                        1L, "Nguyễn Văn A", "12345", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 12 Ngõ 34", true));
        assertTrue(exception.getMessage().contains("Số điện thoại không hợp lệ"));
    }

    @Test
    @DisplayName("Should support copying address with different default status")
    void shouldSupportCopyingWithDefault() {
        Address address =
                new Address(1L, "Nguyễn Văn A", "0987654321", "Hà Nội", "Cầu Giấy", "Dịch Vọng", "Số 12 Ngõ 34", false);

        assertFalse(address.isDefault());
        Address copied = address.withDefault(true);
        assertTrue(copied.isDefault());
        assertEquals(address.getId(), copied.getId());
    }
}
