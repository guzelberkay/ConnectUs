package com.connectus.dto.request;

import java.util.List;

public record ContactUpdateDTO(
        String token,
        Long contactId,
        List<AddressRequestDTO> addresses,
        List<PhoneRequestDTO> phones
) {}
