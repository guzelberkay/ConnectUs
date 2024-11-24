package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProjectUpdateRequestDTO(
        @NotNull String token,
        @NotNull Long projectId,
        String title,
        String description,
        String photo
) {}