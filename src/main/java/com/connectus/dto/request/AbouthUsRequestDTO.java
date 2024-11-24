package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record AbouthUsRequestDTO(@NotNull String token,
                                 @NotNull String content) {
}
