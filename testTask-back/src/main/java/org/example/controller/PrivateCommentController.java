package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.controlleradvice.SimpleResponse;
import org.example.dto.request.RequestCommentDTO;
import org.example.dto.response.LoginDTO;
import org.example.dto.response.PageDTO;
import org.example.dto.response.ResponseCommentDTO;
import org.example.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/private/task/{id}/comment")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class PrivateCommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "Получить все комментарии для задания с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает страницу с комментариями",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDTO.class))),
            @ApiResponse(responseCode = "403", description = "Если у пользователя нет прав админа илли он не исполнитель задания",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Если задание не найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class)))
    })
    public PageDTO<ResponseCommentDTO> getAllCommentsForTask(@PathVariable("id") Long taskID, Pageable pageable) {
        Page<ResponseCommentDTO> commentInfoPage = commentService.getAllCommentsForTask(taskID, pageable);
        return new PageDTO<>(
                commentInfoPage.getContent(),
                commentInfoPage.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @PostMapping
    @Operation(summary = "Создать комментарий к заданию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает страницу с комментариями",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDTO.class))),
            @ApiResponse(responseCode = "403", description = "Если у пользователя нет прав админа или он не исполнитель задания",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Если у введены некорректные данные",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Если задание не найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<ResponseCommentDTO> createComment(@PathVariable("id") Long taskID, @RequestBody RequestCommentDTO requestCommentDTO) {
        return ResponseEntity.ok(commentService.createCommentForAdmin(taskID, requestCommentDTO));
    }


}
