package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.TaskPriority;
import org.example.entity.TaskStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ДТО для получения данных о задании")
public class ResponseTaskDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private ResponseUserDTO creator;
    private Set<ResponseUserDTO> developers;
    private OffsetDateTime createDate;
    private OffsetDateTime updateDate;
}
