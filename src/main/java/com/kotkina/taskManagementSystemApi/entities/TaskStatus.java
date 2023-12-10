package com.kotkina.taskManagementSystemApi.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {
    WAITING("в ожидании"), PROCESSING("в процессе"), FINISHED("завершено");

    private String message;
}
