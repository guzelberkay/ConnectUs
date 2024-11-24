package com.connectus.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(
        @NotNull String firstName,
        @NotNull String lastName,
        @Email @NotNull String email,
        @NotNull String password,
        @NotNull String rePassword
) {}
