package com.connectus.dto.request;

import java.util.List;

public record DeleteRequestDTO(
        String token,
        Long contactId,
        List<Long> addressIds,
        List<Long> phoneIds) {
}
