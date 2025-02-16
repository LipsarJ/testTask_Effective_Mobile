package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.controlleradvice.SimpleResponse;
import org.example.dto.request.RequestTaskDTO;
import org.example.dto.response.LoginDTO;
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

@RestController
@RequestMapping("/api/v1/private/task")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class PrivateTaskController {
    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Получить задания по фильтру и с указаным пользователем в создателях или исполнителях с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает страницу с заданиями",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDTO.class))),
            @ApiResponse(responseCode = "404", description = "Если пользователь не найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "403", description = "Если у пользователя нет прав админа",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class)))
    })
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
    public ResponseEntity<ResponseTaskDTO> getTaskByID(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskByID(id));
    }

    @PostMapping
    @Operation(summary = "Создать новое задание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает ДТО задания",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDTO.class))),
            @ApiResponse(responseCode = "403", description = "Если у пользователя нет прав админа",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Если введены некорректные данные",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<ResponseTaskDTO> createTask(@RequestBody RequestTaskDTO requestTaskDTO) {
        return ResponseEntity.ok(taskService.createTask(requestTaskDTO));
    }

    @PutMapping("{taskID}")
    @Operation(summary = "Изменить задание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает ДТО задания",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDTO.class))),
            @ApiResponse(responseCode = "403", description = "Если у пользователя нет прав админа",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Если введены некорректные данные",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<ResponseTaskDTO> updateTask(@PathVariable Long taskID, @RequestBody RequestTaskDTO requestTaskDTO) {
        return ResponseEntity.ok(taskService.updateTask(taskID, requestTaskDTO));
    }

    @DeleteMapping("{taskID}")
    @Operation(summary = "Удалить задание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление задания",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDTO.class))),
            @ApiResponse(responseCode = "403", description = "Если у пользователя нет прав админа",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskID) {
        taskService.deleteTask(taskID);
        return ResponseEntity.ok().build();
    }
}
