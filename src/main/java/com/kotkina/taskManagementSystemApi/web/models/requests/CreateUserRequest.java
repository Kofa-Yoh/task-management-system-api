package com.kotkina.taskManagementSystemApi.web.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @Schema(description = "Имя пользователя")
    private String name;

    @NotBlank
    @Schema(description = "Email")
    private String email;

    @NotBlank
    @Schema(description = "Пароль")
    private String password;
}
