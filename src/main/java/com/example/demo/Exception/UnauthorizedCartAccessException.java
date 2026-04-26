package com.example.demo.Exception;


public class UnauthorizedCartAccessException extends CartException {
    public UnauthorizedCartAccessException(String message) {
        super(message);
    }

    public UnauthorizedCartAccessException(Long userId, Long cartItemId) {
        super("Felhasználó (ID: " + userId + ") nem módosíthatja ezt a kosár elemet (ID: " + cartItemId + ")");
    }
}

