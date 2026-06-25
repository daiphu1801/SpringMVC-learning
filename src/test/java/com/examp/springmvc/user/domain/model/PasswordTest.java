package com.examp.springmvc.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.examp.springmvc.auth.domain.PasswordHasher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordTest {

    @Test
    @DisplayName("Should create password from hashed string")
    void shouldCreatePasswordFromHashed() {
        Password password = Password.fromHashed("hashed_string");
        assertEquals("hashed_string", password.getHashedValue());
    }

    @Test
    @DisplayName("Should create password from raw string and hasher")
    void shouldCreatePasswordFromRaw() {
        PasswordHasher hasher = mock(PasswordHasher.class);
        when(hasher.hash("raw_password")).thenReturn("hashed_value");

        Password password = Password.fromRaw("raw_password", hasher);
        assertEquals("hashed_value", password.getHashedValue());
    }

    @Test
    @DisplayName("Should throw exception when raw password is too short")
    void shouldThrowExceptionWhenPasswordTooShort() {
        PasswordHasher hasher = mock(PasswordHasher.class);
        assertThrows(IllegalArgumentException.class, () -> Password.fromRaw("12345", hasher));
    }

    @Test
    @DisplayName("Should throw exception when raw password is empty")
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        PasswordHasher hasher = mock(PasswordHasher.class);
        assertThrows(IllegalArgumentException.class, () -> Password.fromRaw("", hasher));
        assertThrows(IllegalArgumentException.class, () -> Password.fromRaw(null, hasher));
    }

    @Test
    @DisplayName("Should verify password match correctly")
    void shouldVerifyPasswordMatch() {
        PasswordHasher hasher = mock(PasswordHasher.class);
        Password password = Password.fromHashed("hashed_value");

        when(hasher.check("correct_pass", "hashed_value")).thenReturn(true);
        when(hasher.check("wrong_pass", "hashed_value")).thenReturn(false);

        assertTrue(password.match("correct_pass", hasher));
        assertFalse(password.match("wrong_pass", hasher));
        assertFalse(password.match(null, hasher));
    }

    @Test
    @DisplayName("Should compare passwords by value equality")
    void shouldComparePasswords() {
        Password p1 = Password.fromHashed("hash1");
        Password p2 = Password.fromHashed("hash1");
        Password p3 = Password.fromHashed("hash2");

        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
