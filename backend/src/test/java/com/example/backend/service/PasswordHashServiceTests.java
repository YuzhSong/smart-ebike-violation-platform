package com.example.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHashServiceTests {
    private final PasswordHashService passwordHashService = new PasswordHashService();

    @Test
    void matchesValidPbkdf2Hash() {
        String hash = "pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDE=$F8EbewShdZ2w/szckhRVL4TvaAzTigz4f8UeG78bwJ0=";

        assertTrue(passwordHashService.matches("Test@123456", hash));
    }

    @Test
    void rejectsWrongPassword() {
        String hash = "pbkdf2_sha256$185000$ZGVtby11c2VyLXNhbHQtMDE=$F8EbewShdZ2w/szckhRVL4TvaAzTigz4f8UeG78bwJ0=";

        assertFalse(passwordHashService.matches("wrong-password", hash));
    }
}
