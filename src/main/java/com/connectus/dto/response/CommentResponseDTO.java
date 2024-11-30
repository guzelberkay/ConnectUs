package com.connectus.dto.response;

public record CommentResponseDTO(
        Long id,              // Yorum ID'si
        Long projectId,
        String companyName,
        String name,
        String surname,
        String email,
        String comment
) {}
