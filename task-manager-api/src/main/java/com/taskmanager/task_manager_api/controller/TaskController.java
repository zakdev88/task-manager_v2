package com.taskmanager.task_manager_api.controller;

import com.taskmanager.task_manager_api.model.dto.*;
import com.taskmanager.task_manager_api.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Task operations
 * Implements only the required endpoints from the coding challenge
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // For frontend integration
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new task
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request) {

        log.info("Creating new task: {}", request.getTitle());

        var taskResponse = taskService.createTask(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    /**
     * Fetch all tasks
     * GET /api/tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        log.info("Fetching all tasks");

        var tasks = taskService.getAllTasks();

        return ResponseEntity.ok(tasks);
    }

    /**
     * Fetch task by ID
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {
        log.info("Fetching task with ID: {}", id);

        var task = taskService.getTaskById(id);

        return ResponseEntity.ok(task);
    }

    /**
     * Update a task
     * PUT /api/tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {

        log.info("Updating task with ID: {} Request: {}", id, request.toString());
        var updatedTask = taskService.updateTask(id, request);

        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Change the status of a task
     * PATCH /api/tasks/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskStatusRequest request) {

        log.info("Updating status for task ID: {} to {}", id, request.getStatus());

        var updatedTask = taskService.updateTaskStatus(id, request);

        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Delete a task
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        log.info("Deleting task with ID: {}", id);

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }
}