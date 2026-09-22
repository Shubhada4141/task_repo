package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    public Task createTask(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(Status.TODO);
        }
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task existingTask = getTaskById(id);

        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setDueDate(taskDetails.getDueDate());
        existingTask.setPriority(taskDetails.getPriority());
        existingTask.setStatus(taskDetails.getStatus());

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        Task existingTask = getTaskById(id);
        taskRepository.delete(existingTask);
    }

    public Task completeTask(Long id) {
        Task existingTask = getTaskById(id);
        existingTask.setStatus(Status.COMPLETED);
        return taskRepository.save(existingTask);
    }

    public List<Task> getTasksByStatus(Status status) {
        return taskRepository.findByStatus(status);
    }
}
