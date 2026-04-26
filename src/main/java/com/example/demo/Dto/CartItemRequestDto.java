package com.example.demo.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDto {

    @NotNull(message = "Termék ID szükséges")
    @Positive(message = "Termék ID pozitív szám kell, hogy legyen")
    private Long productId;

    @NotNull(message = "Mennyiség szükséges")
    @Positive(message = "Mennyiség pozitív szám kell, hogy legyen")
    @Max(value = 1000, message = "Túl nagy mennyiség (maximum 1000)")
    private Integer quantity;
}

