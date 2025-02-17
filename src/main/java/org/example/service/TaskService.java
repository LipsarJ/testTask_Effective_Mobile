package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.controlleradvice.Errors;
import org.example.dto.request.RequestTaskDTO;
import org.example.dto.request.RequestUserDTO;
import org.example.dto.request.UpdateTaskStatusDTO;
import org.example.dto.response.ResponseTaskDTO;
import org.example.entity.Task;
import org.example.entity.TaskPriority;
import org.example.entity.TaskStatus;
import org.example.entity.User;
import org.example.exception.ForbiddenException;
import org.example.exception.extend.CantBeEmptyException;
import org.example.exception.extend.InvalidPriorityException;
import org.example.exception.extend.TaskNotFoundException;
import org.example.exception.extend.UserNotFoundException;
import org.example.mapper.TaskMapper;
import org.example.repo.TaskRepo;
import org.example.repo.UserRepo;
import org.example.repo.filter.FilterParam;
import org.example.repo.spec.TaskSpecification;
import org.example.sequrity.service.UserContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepo taskRepo;
    private final TaskMapper taskMapper;
    private final UserContext userContext;
    private final UserRepo userRepo;

    public Page<ResponseTaskDTO> getTasksForUser(FilterParam filterParam, Pageable pageable) {

        Specification<Task> spec = Specification
                .where(TaskSpecification.searchByFilterText(filterParam.getFilterText()))
                .and(TaskSpecification.searchByFilterUser(filterParam.getUsernames()));

        return taskRepo.findAll(spec, pageable).map(taskMapper::toResponseTaskDTO);
    }

    public ResponseTaskDTO getTaskByID(Long id) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return taskMapper.toResponseTaskDTO(task);
    }

    @Transactional
    public ResponseTaskDTO createTask(RequestTaskDTO requestTaskDTO) {
        Task task = new Task();

        validateTaskData(requestTaskDTO);

        task.setTitle(requestTaskDTO.getTitle());
        task.setDescription(requestTaskDTO.getDescription());

        User user = userRepo.findByUsername(userContext.getUserDTO().getUsername()).orElseThrow(() -> new UserNotFoundException("User is not found"));
        task.setCreator(user);
        Set<RequestUserDTO> requestUserDTOSet = requestTaskDTO.getDevelopers();
        requestUserDTOSet.forEach(userDTO -> {
            User developer = userRepo.findByUsername(userDTO.getUsername()).orElseThrow(()
                    -> new UserNotFoundException("User with username " + userDTO.getUsername() + "is not found"));
            task.getDevelopers().add(developer);
        });

        task.setStatus(TaskStatus.IN_QUEUE);
        task.setPriority(TaskPriority.valueOf(requestTaskDTO.getPriority()));

        taskRepo.save(task);
        return taskMapper.toResponseTaskDTO(task);
    }

    @Transactional
    public ResponseTaskDTO updateTask(Long taskID, RequestTaskDTO requestTaskDTO) {
        Task task = taskRepo.findById(taskID).orElseThrow(() -> new TaskNotFoundException("Task not found"));

        validateTaskData(requestTaskDTO);

        task.setTitle(requestTaskDTO.getTitle());
        task.setDescription(requestTaskDTO.getDescription());
        task.setPriority(TaskPriority.valueOf(requestTaskDTO.getPriority()));
        task.setStatus(TaskStatus.valueOf(requestTaskDTO.getStatus()));

        Set<RequestUserDTO> requestUserDTOSet = requestTaskDTO.getDevelopers();
        requestUserDTOSet.forEach(userDTO -> {
            User developer = userRepo.findByUsername(userDTO.getUsername()).orElseThrow(()
                    -> new UserNotFoundException("User with username " + userDTO.getUsername() + " is not found"));
            task.getDevelopers().add(developer);
        });

        taskRepo.save(task);
        return taskMapper.toResponseTaskDTO(task);
    }

    @Transactional
    public ResponseTaskDTO updateStatus(Long taskID, UpdateTaskStatusDTO updateTaskStatusDTO) {
        User user = userRepo.findByUsername(userContext.getUserDTO().getUsername()).orElseThrow(() -> new UserNotFoundException("User is not found"));
        Task task = taskRepo.findById(taskID).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        if (!task.getDevelopers().contains(user)) {
            throw new ForbiddenException("You are not developer on this task");
        }
        try {
            task.setStatus(TaskStatus.valueOf(updateTaskStatusDTO.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new InvalidPriorityException("Wrong status for task. Try IN_QUEUE, IN_PROCESS or COMPLETED", Errors.PRIORITY_IS_INVALID);
        }

        taskRepo.save(task);
        return taskMapper.toResponseTaskDTO(task);
    }

    public void deleteTask(Long taskID) {
        taskRepo.deleteById(taskID);

    }

    private void validateTaskData(RequestTaskDTO requestTaskDTO) {
        if (requestTaskDTO.getTitle().isEmpty() || requestTaskDTO.getDescription().isEmpty()) {
            throw new CantBeEmptyException("Title and description cant be empty", Errors.CANT_BE_EMPTY);
        }

        try {
            TaskStatus.valueOf(requestTaskDTO.getStatus());
        } catch (IllegalArgumentException e) {
            throw new InvalidPriorityException("Wrong status for task. Try IN_QUEUE for new task, IN_PROCESS or COMPLETED for others", Errors.PRIORITY_IS_INVALID);
        }

        try {
            TaskPriority.valueOf(requestTaskDTO.getPriority());
        } catch (IllegalArgumentException e) {
            throw new InvalidPriorityException("Wrong priority for task. Try HIGH_PRIORITY, MEDIUM_PRIORITY or LOW_PRIORITY", Errors.PRIORITY_IS_INVALID);
        }
    }
}
