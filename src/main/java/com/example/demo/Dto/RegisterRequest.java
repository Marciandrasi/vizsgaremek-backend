package com.example.demo.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Data
public class RegisterRequest {
    @NotBlank(message = "Név kötelező")
    private String name;

    @NotBlank
    @Email(message = "Érvénytelen email formátum")
    private String email;

    @NotBlank(message = "Jelszó kötelező")
    private String password;

    private String phone;
}