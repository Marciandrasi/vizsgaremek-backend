package com.example.demo.Exception;


public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("Termék nem található az ID-val: " + productId);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}

