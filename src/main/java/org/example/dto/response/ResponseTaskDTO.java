package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.entity.TaskPriority;
import org.example.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "ДТО для получения данных о задании")
public class ResponseTaskDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private ResponseUserDTO creator;
    private Set<ResponseUserDTO> developers;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
