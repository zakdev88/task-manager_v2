package com.taskmanager.task_manager_api.model.Enum;

public enum TaskStatus {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
