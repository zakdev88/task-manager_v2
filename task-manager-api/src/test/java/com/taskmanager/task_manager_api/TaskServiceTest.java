package com.taskmanager.task_manager_api;

import com.taskmanager.task_manager_api.model.dto.*;
import com.taskmanager.task_manager_api.exceptions.TaskNotFoundException;
import com.taskmanager.task_manager_api.model.Enum.TaskStatus;
import com.taskmanager.task_manager_api.model.entity.Task;
import com.taskmanager.task_manager_api.repository.TaskRepository;
import com.taskmanager.task_manager_api.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService - Complete coverage for all business logic
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private UUID taskId;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        fixedTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        sampleTask = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();
    }

    @Test
    @DisplayName("Should create task successfully with all fields")
    void shouldCreateTaskSuccessfully() {
        // Given
        var createRequest = CreateTaskRequest.builder()
                .title("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .build();

        var savedTask = Task.builder()
                .id(UUID.randomUUID())
                .title("New Task")
                .description("New Description")
                .status(TaskStatus.TODO)
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        var result = taskService.createTask(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Task");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(result.getCreatedAt()).isEqualTo(fixedTime);
        assertThat(result.getUpdatedAt()).isEqualTo(fixedTime);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should create task with default status when status is null")
    void shouldCreateTaskWithDefaultStatus() {
        // Given
        var createRequest = CreateTaskRequest.builder()
                .title("Task without status")
                .description("Description")
                .status(null) // Null status
                .build();

        var savedTask = Task.builder()
                .id(UUID.randomUUID())
                .title("Task without status")
                .description("Description")
                .status(TaskStatus.TODO) // Should default to TODO
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        var result = taskService.createTask(createRequest);

        // Then
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should get all tasks successfully")
    void shouldGetAllTasksSuccessfully() {
        // Given
        var task1 = Task.builder()
                .id(UUID.randomUUID())
                .title("Task 1")
                .status(TaskStatus.TODO)
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();

        var task2 = Task.builder()
                .id(UUID.randomUUID())
                .title("Task 2")
                .status(TaskStatus.IN_PROGRESS)
                .createdAt(fixedTime.plusHours(1))
                .updatedAt(fixedTime.plusHours(1))
                .build();

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        // When
        List<TaskResponse> result = taskService.getAllTasks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Task 1");
        assertThat(result.get(0).getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(result.get(1).getTitle()).isEqualTo("Task 2");
        assertThat(result.get(1).getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void shouldReturnEmptyListWhenNoTasks() {
        // Given
        when(taskRepository.findAll()).thenReturn(List.of());

        // When
        var result = taskService.getAllTasks();

        // Then
        assertThat(result).isEmpty();
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get task by ID successfully")
    void shouldGetTaskByIdSuccessfully() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));

        // When
        var result = taskService.getTaskById(taskId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when task not found by ID")
    void shouldThrowExceptionWhenTaskNotFoundById() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with ID: " + taskId);

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @DisplayName("Should update task title and description successfully")
    void shouldUpdateTaskSuccessfully() {
        // Given
        var updateRequest = UpdateTaskRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .build();

        var updatedTask = Task.builder()
                .id(taskId)
                .title("Updated Title")
                .description("Updated Description")
                .status(TaskStatus.TODO)
                .createdAt(fixedTime)
                .updatedAt(fixedTime.plusMinutes(30))
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // When
        var result = taskService.updateTask(taskId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO); // Status unchanged

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should update only title when description is null")
    void shouldUpdateOnlyTitleWhenDescriptionIsNull() {
        // Given
        var updateRequest = UpdateTaskRequest.builder()
                .title("Only Title Updated")
                .description(null) // Null description should not update
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // When
        taskService.updateTask(taskId, updateRequest);

        // Then
        verify(taskRepository, times(1)).save(argThat(task ->
                                                              task.getTitle().equals("Only Title Updated") &&
                                                                      task.getDescription().equals("Test Description") // Original description preserved
                                                     ));
    }

    @Test
    @DisplayName("Should not update when title is blank")
    void shouldNotUpdateWhenTitleIsBlank() {
        // Given
        var updateRequest = UpdateTaskRequest.builder()
                .title("   ") // Blank title
                .description("Updated Description")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // When
        taskService.updateTask(taskId, updateRequest);

        // Then
        verify(taskRepository, times(1)).save(argThat(task ->
                                                              task.getTitle().equals("Test Task") && // Original title preserved
                                                                      task.getDescription().equals("Updated Description")
                                                     ));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent task")
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {
        // Given
        var updateRequest = UpdateTaskRequest.builder()
                .title("Updated Title")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateTask(taskId, updateRequest))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with ID: " + taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update task status successfully")
    void shouldUpdateTaskStatusSuccessfully() {
        // Given
        var statusRequest = UpdateTaskStatusRequest.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();

        var updatedTask = Task.builder()
                .id(taskId)
                .title(sampleTask.getTitle())
                .description(sampleTask.getDescription())
                .status(TaskStatus.IN_PROGRESS)
                .createdAt(fixedTime)
                .updatedAt(fixedTime.plusMinutes(15))
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // When
        var result = taskService.updateTaskStatus(taskId, statusRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(result.getTitle()).isEqualTo("Test Task"); // Other fields unchanged

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when updating status to null")
    void shouldThrowExceptionWhenUpdatingStatusToNull() {
        // Given
        var statusRequest = UpdateTaskStatusRequest.builder()
                .status(null)
                .build();

        // When & Then
        assertThatThrownBy(() -> taskService.updateTaskStatus(taskId, statusRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task status cannot be null");

        verify(taskRepository, never()).findById(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating status of non-existent task")
    void shouldThrowExceptionWhenUpdatingStatusOfNonExistentTask() {
        // Given
        var statusRequest = UpdateTaskStatusRequest.builder()
                .status(TaskStatus.DONE)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateTaskStatus(taskId, statusRequest))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with ID: " + taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete task successfully")
    void shouldDeleteTaskSuccessfully() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        // When
        taskService.deleteTask(taskId);

        // Then
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).delete(sampleTask);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with ID: " + taskId);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).delete(any());
    }
}