package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;


public record ProjectSaveRequestDTO(
        @NotNull String token,
        @NotNull String employer, // "Employer" ismi küçük harf ile düzeltilmiş
        @NotNull String title,
        @NotNull String location, // "Location" küçük harf ile
        @NotNull String date,
        @NotNull String description
) {}

