package com.taskmanager.task_manager_api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String message;
    private String error;
    private int status;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String message, String error, int status) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
