package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record PhoneUpdateRequestDTO(
        @NotNull String token,
        @NotNull Long phoneId,
        String description,
        String value
) {
}
