package service;

import org.example.dto.request.RequestTaskDTO;
import org.example.dto.response.ResponseTaskDTO;
import org.example.dto.response.ResponseUserDTO;
import org.example.entity.Task;
import org.example.entity.TaskPriority;
import org.example.entity.TaskStatus;
import org.example.entity.User;
import org.example.mapper.TaskMapper;
import org.example.repo.TaskRepo;
import org.example.repo.UserRepo;
import org.example.sequrity.service.UserContext;
import org.example.service.TaskService;
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
import org.springframework.data.jpa.domain.Specification;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepo taskRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserContext userContext;

    private User testUser;
    private User anotherUser;
    private Task testTask;
    private Pageable pageable;
    private RequestTaskDTO requestTaskDTO;
    private ResponseTaskDTO responseTaskDTO;
    private ResponseUserDTO responseUserDTO;
    private Page<Task> taskPage;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotherUser");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setCreator(testUser);
        testTask.setDevelopers(new HashSet<>(Set.of(testUser)));
        testTask.setStatus(TaskStatus.IN_QUEUE);
        testTask.setPriority(TaskPriority.MEDIUM_PRIORITY);


        responseUserDTO = new ResponseUserDTO();
        responseUserDTO.setId(1L);
        responseUserDTO.setUsername("testUser");

        requestTaskDTO = new RequestTaskDTO();
        requestTaskDTO.setTitle("New Task");
        requestTaskDTO.setDescription("New Description");
        requestTaskDTO.setPriority("MEDIUM_PRIORITY");
        requestTaskDTO.setStatus("IN_QUEUE");
        requestTaskDTO.setDevelopers(new HashSet<>());

        responseTaskDTO = new ResponseTaskDTO();

        pageable = PageRequest.of(0, 10);
        taskPage = new PageImpl<>(List.of(testTask));
    }


    @Test
    void getTasksForUser_ShouldReturnFilteredTasks() {
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(taskRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(taskPage);
        when(taskMapper.toResponseTaskDTO(any(Task.class))).thenReturn(responseTaskDTO);

        Page<ResponseTaskDTO> result = taskService.getTasksForUser("testUser", "", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepo, times(1)).findByUsername("testUser");
        verify(taskRepo, times(1)).findAll(any(Specification.class), eq(pageable));
    }


    @Test
    void createTask_ShouldCreateNewTask() {
        when(userContext.getUserDTO()).thenReturn(responseUserDTO);
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(taskRepo.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponseTaskDTO(any(Task.class))).thenReturn(responseTaskDTO);

        ResponseTaskDTO result = taskService.createTask(requestTaskDTO);

        assertNotNull(result);
        verify(taskRepo, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepo.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponseTaskDTO(any(Task.class))).thenReturn(responseTaskDTO);

        ResponseTaskDTO result = taskService.updateTask(1L, requestTaskDTO);

        assertNotNull(result);
        assertEquals("New Task", testTask.getTitle());
        verify(taskRepo, times(1)).save(any(Task.class));
    }

    @Test
    void updateStatus_ShouldUpdateStatus() {
        when(userContext.getUserDTO()).thenReturn(responseUserDTO);
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(taskRepo.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskMapper.toResponseTaskDTO(any(Task.class))).thenReturn(responseTaskDTO);

        ResponseTaskDTO result = taskService.updateStatus(1L, "COMPLETED");

        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, testTask.getStatus());
        verify(taskRepo, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        doNothing().when(taskRepo).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepo, times(1)).deleteById(1L);
    }
}
