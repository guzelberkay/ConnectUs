package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record AdressRequestDTO(
        @NotNull String token,
        String description,
        String value
) {
}
