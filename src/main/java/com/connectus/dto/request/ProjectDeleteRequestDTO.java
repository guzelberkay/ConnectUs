package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProjectDeleteRequestDTO(@NotNull String token,
                                      @NotNull Long projectId) {
}