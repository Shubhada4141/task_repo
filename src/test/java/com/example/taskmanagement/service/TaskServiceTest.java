package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task(1L, "Complete DevOps Project", "Build CI/CD pipeline", LocalDate.now().plusDays(7), Priority.HIGH, Status.TODO);
        task2 = new Task(2L, "Study Kubernetes", "Learn Pods and Deployments", LocalDate.now().plusDays(3), Priority.MEDIUM, Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should return all tasks")
    void getAllTasks_ReturnsList() {
        given(taskRepository.findAll()).willReturn(List.of(task1, task2));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(2).contains(task1, task2);
        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("Should return task by ID when found")
    void getTaskById_Success() {
        given(taskRepository.findById(1L)).willReturn(Optional.of(task1));

        Task result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Complete DevOps Project");
        verify(taskRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task ID is not found")
    void getTaskById_NotFound_ThrowsException() {
        given(taskRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: 99");

        verify(taskRepository).findById(99L);
    }

    @Test
    @DisplayName("Should create task successfully")
    void createTask_Success() {
        given(taskRepository.save(task1)).willReturn(task1);

        Task created = taskService.createTask(task1);

        assertThat(created).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Complete DevOps Project");
        verify(taskRepository).save(task1);
    }

    @Test
    @DisplayName("Should update task details successfully")
    void updateTask_Success() {
        Task updateDetails = new Task(null, "Updated Title", "Updated Desc", LocalDate.now().plusDays(10), Priority.HIGH, Status.IN_PROGRESS);

        given(taskRepository.findById(1L)).willReturn(Optional.of(task1));
        given(taskRepository.save(any(Task.class))).willAnswer(invocation -> invocation.getArgument(0));

        Task updated = taskService.updateTask(1L, updateDetails);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getStatus()).isEqualTo(Status.IN_PROGRESS);
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_Success() {
        given(taskRepository.findById(1L)).willReturn(Optional.of(task1));
        willDoNothing().given(taskRepository).delete(task1);

        taskService.deleteTask(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task1);
    }

    @Test
    @DisplayName("Should mark task status as COMPLETED")
    void completeTask_Success() {
        given(taskRepository.findById(1L)).willReturn(Optional.of(task1));
        given(taskRepository.save(any(Task.class))).willAnswer(invocation -> invocation.getArgument(0));

        Task completed = taskService.completeTask(1L);

        assertThat(completed.getStatus()).isEqualTo(Status.COMPLETED);
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should return tasks filtered by status")
    void getTasksByStatus_Success() {
        given(taskRepository.findByStatus(Status.TODO)).willReturn(List.of(task1));

        List<Task> result = taskService.getTasksByStatus(Status.TODO);

        assertThat(result).hasSize(1).contains(task1);
        verify(taskRepository).findByStatus(Status.TODO);
    }
}
