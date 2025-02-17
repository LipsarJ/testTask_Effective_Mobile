package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ДТО для получения данных о комментарии")
public class ResponseCommentDTO {
    @Schema(description = "Текст комментария")
    private String text;
    @Schema(description = "Имя создателя комментария")
    private ResponseUserDTO commentator;
    private OffsetDateTime createDate;
    private OffsetDateTime updateDate;
}
