package com.example.demo.Dto;

import com.example.demo.Entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDto {

    @NotBlank(message = "A szállítási cím nem lehet üres")
    private String shippingAddress;

    @NotBlank(message = "A szállítási név nem lehet üres")
    private String shippingName;

    @NotBlank(message = "A szállítási email nem lehet üres")
    private String shippingEmail;

    @NotBlank(message = "A szállítási telefonszám nem lehet üres")
    private String shippingPhone;

    @NotNull(message = "A fizetési mód nem lehet null")
    private PaymentMethod paymentMethod;
}

