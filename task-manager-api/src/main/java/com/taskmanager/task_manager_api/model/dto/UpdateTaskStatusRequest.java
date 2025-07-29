package com.taskmanager.task_manager_api.model.dto;

import com.taskmanager.task_manager_api.model.Enum.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public  class UpdateTaskStatusRequest {
    private TaskStatus status;
}