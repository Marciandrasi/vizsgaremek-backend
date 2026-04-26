package com.example.demo.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Data
public class AuthRequest {
    @NotBlank
    @Email(message = "Érvénytelen email formátum")
    private String email;

    @NotBlank(message = "Jelszó kötelező")
    private String password;
}