package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record OurServicesUpdateRequestDTO(
        @NotNull String token,
        @NotNull Long ourServicesId,
        String title,
        String description,
        String photo
) {}