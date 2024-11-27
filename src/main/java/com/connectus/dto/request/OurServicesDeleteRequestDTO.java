package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

public record OurServicesDeleteRequestDTO(@NotNull String token,
                                          @NotNull Long ourServicesId) {
}