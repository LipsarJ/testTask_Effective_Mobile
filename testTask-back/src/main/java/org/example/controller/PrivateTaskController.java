package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.controlleradvice.CommonErrorApiResponses;
import org.example.controlleradvice.CommonErrorApiResponsesWith404;
import org.example.dto.request.RequestTaskDTO;
import org.example.dto.response.PageDTO;
import org.example.dto.response.ResponseTaskDTO;
import org.example.repo.filter.FilterParam;
import org.example.service.TaskService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.example.constructor.ApiConstants.HTTP_OK_200_DESCRIPTION;

@RestController
@RequestMapping("/api/v1/private/task")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class PrivateTaskController {
    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Получить задания по фильтру и с указаным пользователем в создателях или исполнителях с пагинацией")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageDTO.class)))
    @CommonErrorApiResponsesWith404
    public PageDTO<ResponseTaskDTO> getTasksForUser(@ParameterObject @Valid @Parameter @Schema(description = "Параметры фильтрации") FilterParam filterParam,
                                                    @Schema(name = "Параметры пагинации.", implementation = Pageable.class) Pageable pageable) {
        Page<ResponseTaskDTO> taskInfoPage = taskService.getTasksForUser(filterParam, pageable);
        return new PageDTO<>(
                taskInfoPage.getContent(),
                taskInfoPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить задание по id")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseTaskDTO.class)))
    @CommonErrorApiResponsesWith404
    public ResponseEntity<ResponseTaskDTO> getTaskByID(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskByID(id));
    }

    @PostMapping
    @Operation(summary = "Создать новое задание")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseTaskDTO.class)))
    @CommonErrorApiResponses
    public ResponseEntity<ResponseTaskDTO> createTask(@RequestBody RequestTaskDTO requestTaskDTO) {
        return ResponseEntity.ok(taskService.createTask(requestTaskDTO));
    }

    @PutMapping("{taskID}")
    @Operation(summary = "Изменить задание")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseTaskDTO.class)))
    @CommonErrorApiResponsesWith404
    public ResponseEntity<ResponseTaskDTO> updateTask(@PathVariable Long taskID, @RequestBody RequestTaskDTO requestTaskDTO) {
        return ResponseEntity.ok(taskService.updateTask(taskID, requestTaskDTO));
    }

    @DeleteMapping("{taskID}")
    @Operation(summary = "Удалить задание")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION)
    @CommonErrorApiResponsesWith404

    public ResponseEntity<Void> deleteTask(@PathVariable Long taskID) {
        taskService.deleteTask(taskID);
        return ResponseEntity.ok().build();
    }
}
