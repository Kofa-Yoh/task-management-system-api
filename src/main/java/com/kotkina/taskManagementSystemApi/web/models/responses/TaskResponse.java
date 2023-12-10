package com.kotkina.taskManagementSystemApi.web.models.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private UserResponse author;
    private UserResponse executor;
    private CommentResponseList comments;
}
