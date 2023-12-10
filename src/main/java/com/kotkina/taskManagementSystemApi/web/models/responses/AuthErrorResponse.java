package com.kotkina.taskManagementSystemApi.web.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
}
