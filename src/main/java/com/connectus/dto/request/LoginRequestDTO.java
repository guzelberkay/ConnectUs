package com.connectus.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @Email(message = "Email must be a valid email address.")
        @NotNull String email,

        @NotNull String password
) {}
