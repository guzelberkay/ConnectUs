package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ProjectUpdateRequestDTO(
        @NotNull String token,
        @NotNull Long projectId,
        String title,
        String description,
        MultipartFile photo
) {}