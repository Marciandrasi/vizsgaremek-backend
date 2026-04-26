package com.example.demo.Exception;


public class OrderNotFoundException extends OrderException {
    public OrderNotFoundException(Long orderId) {
        super("Rendelés nem található: ID = " + orderId);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}

