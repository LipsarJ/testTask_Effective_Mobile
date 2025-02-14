package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.controlleradvice.SimpleResponse;
import org.example.dto.request.RequestTaskDTO;
import org.example.dto.response.LoginDTO;
import org.example.dto.response.ResponseTaskDTO;
import org.example.dto.response.TaskInfoPage;
import org.example.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping
    @Operation(summary = "Получить все задания с фильтром и пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает страницу с заданиями",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Если у пользователя нет прав админа",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SimpleResponse.class)))
            })
    public TaskInfoPage getAllTasks(@RequestParam(name = "filterText", required = false) @Schema(example = "Task1") String filterText,
                                    @Schema(name = "Параметры пагинации.", description = "Поле sort должно содержать название поля title " +
                                            "сущности Task", implementation = Pageable.class) Pageable pageable) {
        Page<ResponseTaskDTO> taskInfoPage = taskService.getAllTasks(filterText, pageable);
        return new TaskInfoPage(
                taskInfoPage.getContent(),
                taskInfoPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("{username}")
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
    public TaskInfoPage getTasksForUser(@PathVariable @Schema(example = "Lipsar") String username,
                                        @RequestParam(name = "filterText", required = false) @Schema(example = "Task1") String filterText,
                                        @Schema(name = "Параметры пагинации.", description = "Поле sort должно содержать название поля title " +
                                                "сущности Task", implementation = Pageable.class) Pageable pageable) {
        Page<ResponseTaskDTO> taskInfoPage = taskService.getTasksForUser(username, filterText, pageable);
        return new TaskInfoPage(
                taskInfoPage.getContent(),
                taskInfoPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("create")
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("update/{taskID}")
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

    @PreAuthorize("hasAnyAuthority('ADMIN')")
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

    @PutMapping("update/status/{taskID}")
    @Operation(summary = "Изменить статус задания")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает ДТО задания",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDTO.class))),
            @ApiResponse(responseCode = "403", description = "Если пользователь не является исполнителем задания",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Если задание не найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<ResponseTaskDTO> updateTaskStatus(@PathVariable Long taskID, @RequestBody String status) {
        return ResponseEntity.ok(taskService.updateStatus(taskID, status));
    }
}
