package org.example.dto.response;

public record LoginDTO(
        Long id,
        String username,
        String email
) {
}
