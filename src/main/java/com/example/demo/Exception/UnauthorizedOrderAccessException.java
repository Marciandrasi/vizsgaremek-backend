package com.example.demo.Exception;


public class UnauthorizedOrderAccessException extends OrderException {
    public UnauthorizedOrderAccessException(Long userId, Long orderId) {
        super("Jogosulatlan hozzáférés a rendeléshez. " +
              "Felhasználó ID: " + userId + ", Rendelés ID: " + orderId);
    }

    public UnauthorizedOrderAccessException(String message) {
        super(message);
    }
}

