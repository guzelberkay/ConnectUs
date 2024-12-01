package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record ApproveCommentRequestDTO(
        @NotNull String token,
        @NotNull Long id

) {
}
