package com.example.taskmanagement.config;

import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TaskRepository taskRepository;

    public DataInitializer(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        if (taskRepository.count() == 0) {
            Task task1 = new Task("Complete DevOps Project", "Build CI/CD pipeline with Jenkins & K8s", LocalDate.now().plusDays(7), Priority.HIGH, Status.TODO);
            Task task2 = new Task("Study Kubernetes", "Learn Pods, Deployments & Services", LocalDate.now().plusDays(3), Priority.MEDIUM, Status.IN_PROGRESS);
            Task task3 = new Task("Prepare CI/CD Pipeline", "Write Jenkinsfile and Docker build script", LocalDate.now().plusDays(5), Priority.HIGH, Status.TODO);

            taskRepository.saveAll(List.of(task1, task2, task3));
        }
    }
}
