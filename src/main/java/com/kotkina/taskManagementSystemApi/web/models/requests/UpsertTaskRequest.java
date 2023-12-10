package com.kotkina.taskManagementSystemApi.web.models.requests;

import com.kotkina.taskManagementSystemApi.entities.TaskPriority;
import com.kotkina.taskManagementSystemApi.entities.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertTaskRequest {

    @Schema(description = "Заголовок")
    private String title;

    @Schema(description = "Описание")
    private String description;

    @Schema(description = "Статус", implementation = TaskStatus.class)
    private TaskStatus status;

    @Schema(description = "Приоритет", implementation = TaskPriority.class)
    private TaskPriority priority;

    @Schema(description = "Email исполнителя")
    private String executorEmail;
}
