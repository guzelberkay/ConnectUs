package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record OurServicesSaveRequestDTO(
        @NotNull MultipartFile photo,
        @NotNull String title,
        @NotNull String description,
        @NotNull String token) {
}