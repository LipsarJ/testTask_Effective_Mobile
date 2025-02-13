package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Schema(description = "ДТО с информацией о пользователе")
public class ResponseUserDTO {
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иванов")
    private String username;

    @Schema(description = "Дата обновления", example = "2023-08-24T14:30:00Z")
    private OffsetDateTime updateDate;
}
