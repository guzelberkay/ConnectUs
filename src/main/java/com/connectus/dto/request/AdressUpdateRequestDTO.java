package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record AdressUpdateRequestDTO(
        @NotNull String token,
        @NotNull Long adressId,
        String description,
        String value
) {
}
