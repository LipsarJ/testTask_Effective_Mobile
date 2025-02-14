package org.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ДТО для создания нового комментария")
public class RequestCommentDTO {
    @Schema(description = "Текст комментария", example = "Всё отлично, продолжай")
    private String text;
}
