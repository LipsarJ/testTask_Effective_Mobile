package org.example.dto.response;

import java.util.List;

public record LoginDTO(
        Long id,
        String username,
        String email,
        List<String> roles
) {
}
