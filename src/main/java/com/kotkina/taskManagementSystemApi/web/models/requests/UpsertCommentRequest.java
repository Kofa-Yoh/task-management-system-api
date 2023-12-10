package com.kotkina.taskManagementSystemApi.web.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertCommentRequest {

    @NotBlank
    @Schema(description = "Комментарий")
    private String description;
}
