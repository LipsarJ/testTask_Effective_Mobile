package controller;

import org.example.TestTaskApplication;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Import(WebSecurityConfig.class)
@SpringBootTest(classes = TestTaskApplication.class)
@WithMockUser(username = "Lipsar", authorities = "USER")
@ActiveProfiles("test")
class PublicTaskControllerTest {

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

        mockMvc.perform(get("/api/v1/public/task")
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

        mockMvc.perform(get("/api/v1/public/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void updateTaskStatus_ShouldReturnUpdatedTask() throws Exception {
        Mockito.when(taskService.updateStatus(any(Long.class), any()))
                .thenReturn(responseTask);

        mockMvc.perform(put("/api/v1/public/task/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"Completed\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
