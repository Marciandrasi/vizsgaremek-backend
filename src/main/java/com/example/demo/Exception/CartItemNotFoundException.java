package com.example.demo.Exception;

public class CartItemNotFoundException extends CartException {
    public CartItemNotFoundException(Long cartItemId) {
        super("CartItem nem található az ID-val: " + cartItemId);
    }

    public CartItemNotFoundException(String message) {
        super(message);
    }
}

