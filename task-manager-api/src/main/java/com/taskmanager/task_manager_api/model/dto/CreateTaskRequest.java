package com.taskmanager.task_manager_api.model.dto;

import com.taskmanager.task_manager_api.model.Enum.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTaskRequest {
    @NotBlank(message = "Task title cannot be blank")
    private String title;

    private String description;

    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;
}