package org.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "ДТО для логина пользователя.")
public record LoginUserDTO(

        @NotBlank
        @Schema(description = "Почта для логина", example = "Lipsar@ya.ru")
        String email,

        @NotBlank
        @Schema(description = "Пароль для логина", example = "password")
        String password
) {
}
