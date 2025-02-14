package org.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ДТО для получения пользователей")
public class RequestUserDTO {
    @Schema(description = "Имя пользователя", example = "Lipsar")
    private String username;
}
