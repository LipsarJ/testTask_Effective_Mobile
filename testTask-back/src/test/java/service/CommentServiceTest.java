package service;

import org.example.dto.request.RequestCommentDTO;
import org.example.dto.response.ResponseCommentDTO;
import org.example.dto.response.ResponseRoleDTO;
import org.example.dto.response.ResponseUserDTO;
import org.example.entity.Comment;
import org.example.entity.ERole;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.exception.ForbiddenException;
import org.example.exception.extend.CantBeEmptyException;
import org.example.exception.extend.TaskNotFoundException;
import org.example.mapper.CommentMapper;
import org.example.repo.CommentRepo;
import org.example.repo.TaskRepo;
import org.example.repo.UserRepo;
import org.example.sequrity.service.UserContext;
import org.example.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepo commentRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TaskRepo taskRepo;

    @Mock
    private UserContext userContext;

    private User testUser;
    private User anotherUser;
    private Task testTask;
    private RequestCommentDTO requestCommentDTO;
    private ResponseCommentDTO responseCommentDTO;
    private ResponseUserDTO responseUserDTO;
    private ResponseUserDTO responseAnotherUserDTO;
    private Page<Comment> commentPage;
    private Pageable pageable;


    @BeforeEach
    void setUp() {
        ResponseRoleDTO responseRoleDTO = new ResponseRoleDTO();
        responseRoleDTO.setRoleName(ERole.USER);

        Set<ResponseRoleDTO> responseRoleDTOSet = new HashSet<>();
        responseRoleDTOSet.add(responseRoleDTO);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setRoles(new HashSet<>());

        responseUserDTO = new ResponseUserDTO();
        responseUserDTO.setId(1L);
        responseUserDTO.setUsername("testUser");
        responseUserDTO.setRoles(responseRoleDTOSet);

        ResponseRoleDTO responseAnotherRoleDTO = new ResponseRoleDTO();
        responseAnotherRoleDTO.setRoleName(ERole.ADMIN);

        Set<ResponseRoleDTO> responseAnotherRoleDTOSet = new HashSet<>();
        responseAnotherRoleDTOSet.add(responseAnotherRoleDTO);

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotherUser");
        anotherUser.setRoles(new HashSet<>());

        responseAnotherUserDTO = new ResponseUserDTO();
        responseAnotherUserDTO.setId(2L);
        responseAnotherUserDTO.setUsername("anotherUser");
        responseAnotherUserDTO.setRoles(responseAnotherRoleDTOSet);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");

        requestCommentDTO = new RequestCommentDTO();
        requestCommentDTO.setText("Test Comment");

        responseCommentDTO = new ResponseCommentDTO();
        responseCommentDTO.setText("Test Comment");

        pageable = PageRequest.of(0, 10);
        commentPage = new PageImpl<>(List.of(new Comment()));
    }

    @Test
    void getAllCommentsForTask_ShouldReturnComments() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(testTask));
        when(commentRepo.findByTask(testTask, pageable)).thenReturn(commentPage);
        when(commentMapper.toResponseCommentDTO(any(Comment.class))).thenReturn(responseCommentDTO);

        Page<ResponseCommentDTO> result = commentService.getAllCommentsForTask(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commentRepo, times(1)).findByTask(testTask, pageable);
    }

    @Test
    void createComment_ShouldCreateComment() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(testTask));
        when(userContext.getUserDTO()).thenReturn(responseAnotherUserDTO);
        when(userRepo.findByUsername("anotherUser")).thenReturn(Optional.of(anotherUser));
        when(commentMapper.toResponseCommentDTO(any(Comment.class))).thenReturn(responseCommentDTO);
        when(commentRepo.save(any(Comment.class))).thenReturn(new Comment());

        ResponseCommentDTO result = commentService.createCommentForAdmin(1L, requestCommentDTO);

        assertNotNull(result);
        verify(commentRepo, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_ShouldThrowExceptionIfTextIsEmpty() {
        requestCommentDTO.setText("");
        when(taskRepo.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepo.findByUsername("anotherUser")).thenReturn(Optional.of(anotherUser));
        when(userContext.getUserDTO()).thenReturn(responseAnotherUserDTO);

        assertThrows(CantBeEmptyException.class, () -> commentService.createCommentForAdmin(1L, requestCommentDTO));
    }

    @Test
    void createComment_ShouldThrowExceptionIfUserNotAllowed() {
        Set<User> developers = new HashSet<>();
        testTask.setDevelopers(developers);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(userContext.getUserDTO()).thenReturn(responseUserDTO);

        assertThrows(ForbiddenException.class, () -> commentService.createCommentForDeveloper(1L, requestCommentDTO));
    }

    @Test
    void createComment_ShouldThrowExceptionIfTaskNotFound() {
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> commentService.createCommentForAdmin(1L, requestCommentDTO));
    }
}
