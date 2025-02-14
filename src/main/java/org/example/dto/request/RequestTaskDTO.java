package org.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ДТО для создания и редактирования заданий")
public class RequestTaskDTO {
    @NotBlank
    @Schema(description = "Заголовок задания", example = "Найти баг")
    private String title;
    @NotBlank
    @Schema(description = "Описание задания", example = "При создании задания, ставится неверный статус")
    private String description;
    @Schema(description = "Статус задания", example = "IN_QUEUE")
    private String status;
    @NotBlank
    @Schema(description = "Приоритет задания", example = "HIGH_PRIORITY")
    private String priority;
    @Schema(description = "Список исполнителей", example = "[\"Lipsar\", \"Andrey_V\"]")
    private Set<RequestUserDTO> developers;
}
