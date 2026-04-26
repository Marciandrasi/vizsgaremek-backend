package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;

    @NotBlank(message = "A termék neve nem lehet üres")
    private String name;

    private String description;

    @NotNull(message = "Az ár nem lehet null")
    @Positive(message = "Az ár nagyobb kell, hogy legyen, mint 0")
    private BigDecimal price;

    private String imageUrl;
}

