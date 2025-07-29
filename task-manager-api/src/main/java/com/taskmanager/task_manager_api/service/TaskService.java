package com.taskmanager.task_manager_api.service;
import com.taskmanager.task_manager_api.model.Enum.TaskStatus;
import com.taskmanager.task_manager_api.model.dto.*;
import com.taskmanager.task_manager_api.model.entity.Task;
import com.taskmanager.task_manager_api.exceptions.TaskNotFoundException;
import com.taskmanager.task_manager_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for Task operations
 * Contains only the required CRUD operations from the coding challenge
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Create a new task
     */
    public TaskResponse createTask(CreateTaskRequest request) {
        log.info("Creating new task with title: {}", request.getTitle());

        var taskStatus = request.getStatus() != null ? request.getStatus() : TaskStatus.TODO;

        var task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(taskStatus)
                .build();

        var savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());

        return convertToDto(savedTask);
    }

    /**
     * Fetch all tasks
     */
    public List<TaskResponse> getAllTasks() {
        var tasks = taskRepository.findAll();

        return tasks.stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Fetch task by ID
     */
    public TaskResponse getTaskById(UUID id) {
        return taskRepository.findById(id)
                .map(task -> {
                    return convertToDto(task);
                })
                .orElseThrow(() -> {
                    log.warn("Task not found with ID: {}", id);
                    return new TaskNotFoundException("Task not found with ID: " + id);
                });
    }

    /**
     * Update a task
     */
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with ID: {}", id);
                    return new TaskNotFoundException("Task not found with ID: " + id);
                });

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle().strip());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription().strip());
        }

        if(request.getTaskStatus() != null){
            task.setStatus(request.getTaskStatus());
        }

        var updatedTask = taskRepository.save(task);
        log.info("Task updated successfully: {}", updatedTask.getTitle());

        return convertToDto(updatedTask);
    }

    /**
     * Change the status of a task
     */
    public TaskResponse updateTaskStatus(UUID id, UpdateTaskStatusRequest request) {
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Task status cannot be null");
        }

        var task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with ID: {}", id);
                    return new TaskNotFoundException("Task not found with ID: " + id);
                });

        task.setStatus(request.getStatus());
        var updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }

    /**
     * Delete a task
     */
    public void deleteTask(UUID id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with ID: {}", id);
                    return new TaskNotFoundException("Task not found with ID: " + id);
                });

        taskRepository.delete(task);
        log.info("Task deleted successfully: {}", task.getTitle());
    }

    /**
     * Convert Task entity to TaskResponse DTO
     */
    private TaskResponse convertToDto(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}