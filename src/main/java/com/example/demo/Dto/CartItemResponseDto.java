package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * CartItem válasz DTO
 * Lazy Loading problémákat elkerülendő exponálunk egy DTO-t
 * helyett a raw CartItem entitást.
 *
 * Ez a DTO biztosítja, hogy az User és Product relációk
 * nem szerzializálódnak Hibernate proxy-ként.
 */
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

