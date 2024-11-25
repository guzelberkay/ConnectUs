package com.connectus.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CommentSaveRequestDTO(
        @NotNull Long projectId,
        @NotNull String name,
        @NotNull String surname,
        @NotNull String email,
        @NotNull String comment
) {
}
