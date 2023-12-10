package com.kotkina.taskManagementSystemApi.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskPriority {
    HIGH("высокий"), MEDIUM("средний"), LOW("низкий");

    private String title;
}
