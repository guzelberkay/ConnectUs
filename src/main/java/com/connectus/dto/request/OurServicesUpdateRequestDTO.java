package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record OurServicesUpdateRequestDTO(
        @NotNull String token,
        @NotNull Long ourServicesId,
        @NotNull String title,
        @NotNull String description,
        @NotNull MultipartFile photo
) {}