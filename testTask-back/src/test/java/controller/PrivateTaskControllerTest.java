package controller;

import org.example.TestTaskApplication;
import org.example.dto.request.RequestTaskDTO;
import org.example.dto.response.ResponseTaskDTO;
import org.example.repo.filter.FilterParam;
import org.example.sequrity.WebSecurityConfig;
import org.example.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Import(WebSecurityConfig.class)
@SpringBootTest(classes = TestTaskApplication.class)
@WithMockUser(username = "Lipsar", authorities = "ADMIN")
class PrivateTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private ResponseTaskDTO responseTask;

    @BeforeEach
    void setUp() {
        responseTask = new ResponseTaskDTO();
        responseTask.setId(1L);
        responseTask.setTitle("Test Task");
    }

    @Test
    void getTasksForUser_ShouldReturnTaskList() throws Exception {
        Page<ResponseTaskDTO> taskPage = new PageImpl<>(List.of(responseTask));
        Mockito.when(taskService.getTasksForUser(any(FilterParam.class), any(PageRequest.class)))
                .thenReturn(taskPage);

        mockMvc.perform(get("/api/v1/private/task")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.info[0].id").value(1));
    }

    @Test
    void getTaskByID_ShouldReturnTask() throws Exception {
        Mockito.when(taskService.getTaskByID(1L)).thenReturn(responseTask);

        mockMvc.perform(get("/api/v1/private/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        RequestTaskDTO requestTaskDTO = new RequestTaskDTO();
        requestTaskDTO.setTitle("New Task");
        Mockito.when(taskService.createTask(any(RequestTaskDTO.class))).thenReturn(responseTask);

        mockMvc.perform(post("/api/v1/private/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"New Task\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        RequestTaskDTO requestTaskDTO = new RequestTaskDTO();
        requestTaskDTO.setTitle("Updated Task");
        Mockito.when(taskService.updateTask(any(Long.class), any(RequestTaskDTO.class)))
                .thenReturn(responseTask);

        mockMvc.perform(put("/api/v1/private/task/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Updated Task\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void deleteTask_ShouldReturnOk() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/v1/private/task/1"))
                .andExpect(status().isOk());
    }
}

