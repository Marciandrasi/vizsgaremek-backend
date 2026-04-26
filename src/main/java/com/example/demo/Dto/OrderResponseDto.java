package com.example.demo.Dto;

import com.example.demo.Entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String shippingName;
    private String shippingEmail;
    private String shippingPhone;
    private PaymentMethod paymentMethod;
    private boolean isPaid;
    private List<OrderItemResponseDto> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

