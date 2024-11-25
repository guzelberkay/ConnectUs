package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record AboutUsRequestDTO(@NotNull String token,
                                @NotNull String content) {
}
