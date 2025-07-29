package com.taskmanager.task_manager_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.task_manager_api.model.Enum.TaskStatus;
import com.taskmanager.task_manager_api.model.dto.*;
import com.taskmanager.task_manager_api.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TaskController
 * Tests the complete web layer with real database interactions
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Task Controller Integration Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create task successfully and return 201 Created")
    void shouldCreateTaskSuccessfully() throws Exception {
        var createRequest = CreateTaskRequest.builder()
                .title("Integration Test Task")
                .description("Testing task creation via REST API")
                .status(TaskStatus.TODO)
                .build();

        MvcResult result = mockMvc.perform(post("/api/tasks")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.description").value("Testing task creation via REST API"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andReturn();

        // Verify task was saved to database
        assertThat(taskRepository.count()).isEqualTo(1);

        // Extract and verify the created task
        String responseContent = result.getResponse().getContentAsString();
        TaskResponse createdTask = objectMapper.readValue(responseContent, TaskResponse.class);
        assertThat(createdTask.getId()).isNotNull();
        assertThat(createdTask.getTitle()).isEqualTo("Integration Test Task");
    }

    @Test
    @DisplayName("Should create task with default status when status is null")
    void shouldCreateTaskWithDefaultStatus() throws Exception {
        // Given
        var createRequest = CreateTaskRequest.builder()
                .title("Task Without Status")
                .description("Should default to TODO")
                .status(null)
                .build();

        mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.title").value("Task Without Status"));

        // Verify in database
        var taskFromDb = taskRepository.findAll().get(0);
        assertThat(taskFromDb.getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating task with blank title")
    void shouldReturnBadRequestForBlankTitle() throws Exception {
        // Given
        var invalidRequest = CreateTaskRequest.builder()
                .title("")
                .description("Valid description")
                .build();

        // When & Then
        mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        // Verify no task was created
        assertThat(taskRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should get all tasks successfully")
    void shouldGetAllTasksSuccessfully() throws Exception {
        createTestTask("First Task", "First description", TaskStatus.TODO);
        createTestTask("Second Task", "Second description", TaskStatus.IN_PROGRESS);
        createTestTask("Third Task", "Third description", TaskStatus.DONE);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].title").value(
                        org.hamcrest.Matchers.containsInAnyOrder(
                                "First Task", "Second Task", "Third Task")));

        assertThat(taskRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return empty array when no tasks exist")
    void shouldReturnEmptyArrayWhenNoTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should get task by ID successfully")
    void shouldGetTaskByIdSuccessfully() throws Exception {
        // Given
        UUID taskId = createTestTask("Test Task", "Test description", TaskStatus.TODO);

        // When & Then
        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("Should return 404 when getting non-existent task")
    void shouldReturn404WhenTaskNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/tasks/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with ID: " + nonExistentId));
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() throws Exception {
        UUID taskId = createTestTask("Original Title", "Original description", TaskStatus.TODO);

        var updateRequest = UpdateTaskRequest.builder()
                .title("Updated Title")
                .description("Updated description")
                .build();

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.status").value("TODO")); // Status should remain unchanged

        var updatedTask = taskRepository.findById(taskId).orElseThrow();
        assertThat(updatedTask.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTask.getDescription()).isEqualTo("Updated description");
        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    @DisplayName("Should update only title when description is null")
    void shouldUpdateOnlyTitleWhenDescriptionIsNull() throws Exception {
        // Given
        UUID taskId = createTestTask("Original Title", "Original description", TaskStatus.TODO);

        var updateRequest = UpdateTaskRequest.builder()
                .title("Only Title Updated")
                .description(null)
                .build();

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Only Title Updated"))
                .andExpect(jsonPath("$.description").value("Original description"));

        var updatedTask = taskRepository.findById(taskId).orElseThrow();
        assertThat(updatedTask.getTitle()).isEqualTo("Only Title Updated");
        assertThat(updatedTask.getDescription()).isEqualTo("Original description");
    }

    @Test
    @DisplayName("Should update task status successfully")
    void shouldUpdateTaskStatusSuccessfully() throws Exception {
        UUID taskId = createTestTask("Test Task", "Test description", TaskStatus.TODO);

        var statusRequest = UpdateTaskStatusRequest.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();

        mockMvc.perform(patch("/api/tasks/{id}/status", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.title").value("Test Task")); // Other fields should remain unchanged

        var updatedTask = taskRepository.findById(taskId).orElseThrow();
        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }


    @Test
    @DisplayName("Should return 404 when updating status of non-existent task")
    void shouldReturn404WhenUpdatingStatusOfNonExistentTask() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        var statusRequest = UpdateTaskStatusRequest.builder()
                .status(TaskStatus.DONE)
                .build();

        mockMvc.perform(patch("/api/tasks/{id}/status", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with ID: " + nonExistentId));
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent task")
    void shouldReturn404WhenDeletingNonExistentTask() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/tasks/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with ID: " + nonExistentId));
    }

    @Test
    @DisplayName("Should handle complete CRUD workflow")
    @Transactional
    void shouldHandleCompleteCrudWorkflow() throws Exception {

        var createRequest = CreateTaskRequest.builder()
                .title("CRUD Workflow Task")
                .description("Testing complete CRUD operations")
                .status(TaskStatus.TODO)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                                                         .contentType(MediaType.APPLICATION_JSON)
                                                         .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseContent = createResult.getResponse().getContentAsString();
        TaskResponse createdTask = objectMapper.readValue(createResponseContent, TaskResponse.class);
        UUID taskId = createdTask.getId();

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("CRUD Workflow Task"));

        var updateRequest = UpdateTaskRequest.builder()
                .title("Updated CRUD Task")
                .description("Updated description")
                .build();

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated CRUD Task"));

        var statusRequest = UpdateTaskStatusRequest.builder()
                .status(TaskStatus.DONE)
                .build();

        mockMvc.perform(patch("/api/tasks/{id}/status", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle invalid JSON in request body")
    void shouldHandleInvalidJSON() throws Exception {
        mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ invalid json }"))
                .andExpect(status().is5xxServerError());
    }

    /**
     * Helper method to create a test task and return its ID
     */
    private UUID createTestTask(String title, String description, TaskStatus status) throws Exception {
        var createRequest = CreateTaskRequest.builder()
                .title(title)
                .description(description)
                .status(status)
                .build();

        MvcResult result = mockMvc.perform(post("/api/tasks")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        TaskResponse createdTask = objectMapper.readValue(responseContent, TaskResponse.class);
        return createdTask.getId();
    }
}