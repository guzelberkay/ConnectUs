package com.connectus.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ContactSaveRequestDTO(
        @NotNull String token,
        List<AddressRequestDTO> addresses,
        List<PhoneRequestDTO> phones
) {}