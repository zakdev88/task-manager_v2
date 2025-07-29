package com.taskmanager.task_manager_api.repository;

import com.taskmanager.task_manager_api.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
