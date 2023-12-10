package com.kotkina.taskManagementSystemApi.web.models.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentResponse {
    private String description;
    private UserResponse user;
}
