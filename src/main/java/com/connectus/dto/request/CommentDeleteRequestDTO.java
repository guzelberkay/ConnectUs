package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record CommentDeleteRequestDTO(
        @NotNull String token,
        @NotNull Long commentId
) {
}
