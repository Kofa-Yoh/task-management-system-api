package com.kotkina.taskManagementSystemApi.web.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private Long id;

    private String token;

    private String refreshToken;

    private String username;

    private String email;

    private List<String> roles;
}
