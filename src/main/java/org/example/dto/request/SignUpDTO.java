package org.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "ДТО для регистрации пользователя.")
public record SignUpDTO(
        @NotBlank
        @Size(min = 3, max = 20)
        @Schema(description = "Имя пользователя для регистрации", example = "Lipsar")
        String username,

        @NotBlank
        @Size(min = 6, max = 20)
        @Schema(description = "Пароль для регистрации", example = "password")
        String password,

        @NotBlank
        @Email
        @Size(max = 30)
        @Schema(description = "Почта для регистрации", example = "Lipsar@ya.ru")
        String email
) {

}

