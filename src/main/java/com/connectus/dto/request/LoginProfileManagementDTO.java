package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;



public record LoginProfileManagementDTO(
        @NotNull  String password
) {
}
