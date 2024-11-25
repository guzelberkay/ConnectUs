package com.connectus.dto.request;

public record PhoneRequestDTO(
        Long id,            // ID is optional for updating existing phones
        String description, // Description of the phone (e.g., "Mobile", "Work")
        String value        // The actual phone number (e.g., "123-456-7890")
) {
}
