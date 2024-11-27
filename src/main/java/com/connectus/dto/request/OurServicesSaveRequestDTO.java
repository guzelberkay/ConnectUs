package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record OurServicesSaveRequestDTO(@NotNull String photo,
                                        @NotNull String title,
                                        @NotNull String description,
                                        @NotNull String token) {
}
