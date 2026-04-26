package com.example.demo.Exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("Felhasználó nem található az alábbi email címmel: " + email);
    }

    public UserNotFoundException(Long userId) {
        super("Felhasználó nem található az alábbi ID-vel: " + userId);
    }
}

