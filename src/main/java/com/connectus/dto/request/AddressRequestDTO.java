package com.connectus.dto.request;

public record AddressRequestDTO(Long id,
                                String description, // Description of the address (e.g., "Home", "Office")
                                String value        // The actual address value (e.g., "123 Main St")
) {
}
