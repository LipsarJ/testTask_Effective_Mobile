package org.example.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.RequestTaskDTO;
import org.example.dto.response.ResponseTaskDTO;
import org.example.dto.response.TaskInfoPage;
import org.example.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("{username}")
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
    public ResponseEntity<ResponseTaskDTO> createTask(@RequestBody RequestTaskDTO requestTaskDTO) {
        return ResponseEntity.ok(taskService.createTask(requestTaskDTO));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("update/{taskID}")
    public ResponseEntity<ResponseTaskDTO> updateTask(@PathVariable Long taskID, @RequestBody RequestTaskDTO requestTaskDTO) {
        return ResponseEntity.ok(taskService.updateTask(taskID, requestTaskDTO));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("{taskID}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskID) {
        taskService.deleteTask(taskID);
        return ResponseEntity.ok().build();
    }

    @PutMapping("update/status/{taskID}")
    public ResponseEntity<ResponseTaskDTO> updateTaskStatus(@PathVariable Long taskID, @RequestBody String status) {
        return ResponseEntity.ok(taskService.updateStatus(taskID, status));
    }
}
