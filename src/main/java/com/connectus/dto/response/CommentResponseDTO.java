package com.connectus.dto.response;

import com.connectus.entity.enums.EStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record CommentResponseDTO(
        Long id,              // Yorum ID'si
        EStatus status,
        Long projectId,
        String companyName,
        String name,
        String surname,
        String email,
        String comment
) {}
