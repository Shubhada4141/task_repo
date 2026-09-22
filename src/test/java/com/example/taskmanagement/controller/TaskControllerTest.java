package com.example.taskmanagement.controller;

import com.example.taskmanagement.exception.GlobalExceptionHandler;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task(1L, "Complete DevOps Project", "Build CI/CD pipeline", LocalDate.now().plusDays(7), Priority.HIGH, Status.TODO);
        task2 = new Task(2L, "Study Kubernetes", "Learn Pods and Deployments", LocalDate.now().plusDays(3), Priority.MEDIUM, Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("GET /api/tasks - Success 200 OK")
    void getAllTasks_Success() throws Exception {
        given(taskService.getAllTasks()).willReturn(List.of(task1, task2));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Complete DevOps Project")))
                .andExpect(jsonPath("$[1].title", is("Study Kubernetes")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Success 200 OK")
    void getTaskById_Success() throws Exception {
        given(taskService.getTaskById(1L)).willReturn(task1);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Complete DevOps Project")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Not Found 404")
    void getTaskById_NotFound() throws Exception {
        given(taskService.getTaskById(99L)).willThrow(new ResourceNotFoundException("Task not found with id: 99"));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Task not found with id: 99")));
    }

    @Test
    @DisplayName("POST /api/tasks - Created 201")
    void createTask_Success() throws Exception {
        given(taskService.createTask(any(Task.class))).willReturn(task1);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Complete DevOps Project")));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - Success 200 OK")
    void updateTask_Success() throws Exception {
        given(taskService.updateTask(eq(1L), any(Task.class))).willReturn(task1);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Complete DevOps Project")));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Success 204 No Content")
    void deleteTask_Success() throws Exception {
        willDoNothing().given(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/complete - Success 200 OK")
    void completeTask_Success() throws Exception {
        Task completedTask = new Task(1L, "Complete DevOps Project", "Build CI/CD pipeline", LocalDate.now().plusDays(7), Priority.HIGH, Status.COMPLETED);
        given(taskService.completeTask(1L)).willReturn(completedTask);

        mockMvc.perform(patch("/api/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    @DisplayName("GET /api/tasks/status/{status} - Success 200 OK")
    void getTasksByStatus_Success() throws Exception {
        given(taskService.getTasksByStatus(Status.TODO)).willReturn(List.of(task1));

        mockMvc.perform(get("/api/tasks/status/TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("TODO")));
    }
}
