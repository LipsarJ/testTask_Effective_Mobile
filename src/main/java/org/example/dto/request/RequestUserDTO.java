package org.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "ДТО для получения пользователей")
@AllArgsConstructor
@NoArgsConstructor
public class RequestUserDTO {
    @Schema(description = "Имя пользователя", example = "Lipsar")
    private String username;
}
