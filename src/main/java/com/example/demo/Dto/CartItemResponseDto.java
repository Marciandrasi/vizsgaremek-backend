package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDto {
    private Long id;

    private Long userId;
    private String userEmail;

    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private String productDescription;
    private String productImageUrl;

    private Integer quantity;
}

