package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.controlleradvice.Errors;
import org.example.dto.request.RequestCommentDTO;
import org.example.dto.response.ResponseCommentDTO;
import org.example.entity.Comment;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.exception.ForbiddenException;
import org.example.exception.extend.CantBeEmptyException;
import org.example.exception.extend.TaskNotFoundException;
import org.example.exception.extend.UserNotFoundException;
import org.example.mapper.CommentMapper;
import org.example.repo.CommentRepo;
import org.example.repo.TaskRepo;
import org.example.repo.UserRepo;
import org.example.sequrity.service.UserContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo commentRepo;
    private final UserRepo userRepo;
    private final CommentMapper commentMapper;
    private final TaskRepo taskRepo;
    private final UserContext userContext;

    public Page<ResponseCommentDTO> getAllCommentsForTask(Long taskID, Pageable pageable) {
        Task task = taskRepo.findById(taskID).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        Page<Comment> comments = commentRepo.findByTask(task, pageable);
        return comments.map(commentMapper::toResponseCommentDTO);

    }

    @Transactional
    public ResponseCommentDTO createCommentForAdmin(Long taskID, RequestCommentDTO requestCommentDTO) {
        Task task = taskRepo.findById(taskID).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        User user = userRepo.findByUsername(userContext.getUserDTO().getUsername()).orElseThrow(() -> new UserNotFoundException("User not found"));

        return createComment(task, user, requestCommentDTO);
    }

    @Transactional
    public ResponseCommentDTO createCommentForDeveloper(Long taskID, RequestCommentDTO requestCommentDTO) {
        Task task = taskRepo.findById(taskID).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        User user = userRepo.findByUsername(userContext.getUserDTO().getUsername()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!task.getDevelopers().contains(user)) {
            throw new ForbiddenException("You are not allowed to add comments for this task");
        }

        return createComment(task, user, requestCommentDTO);
    }

    @Transactional
    ResponseCommentDTO createComment(Task task, User user, RequestCommentDTO requestCommentDTO) {
        if (requestCommentDTO.getText().isEmpty()) {
            throw new CantBeEmptyException("Comment's text cant be empty", Errors.CANT_BE_EMPTY);
        }

        Comment comment = new Comment();
        comment.setText(requestCommentDTO.getText());
        comment.setCommentator(user);
        comment.setTask(task);
        commentRepo.save(comment);
        return commentMapper.toResponseCommentDTO(comment);
    }
}
