package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.example.controlleradvice.CommonErrorApiResponsesWith404;
import org.example.dto.request.RequestCommentDTO;
import org.example.dto.response.PageDTO;
import org.example.dto.response.ResponseCommentDTO;
import org.example.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.example.constructor.ApiConstants.HTTP_OK_200_DESCRIPTION;
import static org.example.constructor.ApiConstants.PRIVATE_V1_COMMENTS;

@RestController
@RequestMapping(PRIVATE_V1_COMMENTS)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class PrivateCommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "Получить все комментарии для задания с пагинацией")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageDTO.class)))
    @CommonErrorApiResponsesWith404
    public PageDTO<ResponseCommentDTO> getAllCommentsForTask(@PathVariable("taskId") Long taskID, Pageable pageable) {
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
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseCommentDTO.class)))
    @CommonErrorApiResponsesWith404
    public ResponseEntity<ResponseCommentDTO> createComment(@PathVariable("taskId") Long taskID, @RequestBody RequestCommentDTO requestCommentDTO) {
        return ResponseEntity.ok(commentService.createCommentForAdmin(taskID, requestCommentDTO));
    }


}
