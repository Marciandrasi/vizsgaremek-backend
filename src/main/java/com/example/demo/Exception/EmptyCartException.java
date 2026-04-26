package com.example.demo.Exception;


public class EmptyCartException extends OrderException {
    public EmptyCartException(Long userId) {
        super("Nem lehet rendelést leadni üres kosárral. Felhasználó ID: " + userId);
    }

    public EmptyCartException(String message) {
        super(message);
    }
}

