package controller;

import org.example.TestTaskApplication;
import org.example.dto.request.RequestCommentDTO;
import org.example.dto.response.ResponseCommentDTO;
import org.example.sequrity.WebSecurityConfig;
import org.example.service.CommentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Import(WebSecurityConfig.class)
@SpringBootTest(classes = TestTaskApplication.class)
@ActiveProfiles("test")
class PrivateCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private ResponseCommentDTO responseComment;

    @BeforeEach
    void setUp() {
        responseComment = new ResponseCommentDTO();
        responseComment.setText("Test Admin Comment");
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void getAllCommentsForTask_ShouldReturnCommentList_ForAdmin() throws Exception {
        Page<ResponseCommentDTO> commentPage = new PageImpl<>(List.of(responseComment));
        Mockito.when(commentService.getAllCommentsForTask(any(Long.class), any(PageRequest.class)))
                .thenReturn(commentPage);

        mockMvc.perform(get("/api/v1/private/task/1/comment")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.info[0].text").value("Test Admin Comment"));
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void getAllCommentsForTask_ShouldReturnForbidden_ForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/private/task/1/comment")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void createComment_ShouldReturnCreatedComment_ForAdmin() throws Exception {
        RequestCommentDTO requestCommentDTO = new RequestCommentDTO();
        requestCommentDTO.setText("New Admin Comment");
        Mockito.when(commentService.createCommentForAdmin(any(Long.class), any(RequestCommentDTO.class)))
                .thenReturn(responseComment);

        mockMvc.perform(post("/api/v1/private/task/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"text\": \"New Admin Comment\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test Admin Comment"));
    }

    @Test
    @WithMockUser(username = "user", authorities = "USER")
    void createComment_ShouldReturnForbidden_ForNonAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/private/task/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"text\": \"New Admin Comment\" }"))
                .andExpect(status().isForbidden());
    }
}
