package com.examp.springmvc.auth.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher hasher = new BCryptPasswordHasher();

    @Test
    @DisplayName("Should hash and verify password correctly")
    void shouldHashAndVerifyPassword() {
        String raw = "my_secure_password";
        String hashed = hasher.hash(raw);

        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$"));

        assertTrue(hasher.check(raw, hashed));
        assertFalse(hasher.check("wrong_password", hashed));
    }
}
