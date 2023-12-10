package com.kotkina.taskManagementSystemApi.web.controllers;

import com.kotkina.taskManagementSystemApi.exceptions.AlreadyExistsException;
import com.kotkina.taskManagementSystemApi.repositories.UserRepository;
import com.kotkina.taskManagementSystemApi.security.SecurityService;
import com.kotkina.taskManagementSystemApi.web.models.responses.AuthErrorResponse;
import com.kotkina.taskManagementSystemApi.web.models.requests.CreateUserRequest;
import com.kotkina.taskManagementSystemApi.web.models.requests.LoginRequest;
import com.kotkina.taskManagementSystemApi.web.models.requests.RefreshTokenRequest;
import com.kotkina.taskManagementSystemApi.web.models.responses.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Пользователи")
public class AuthController {

    private final UserRepository userRepository;

    private final SecurityService securityService;

    @Operation(summary = "Новый пользователь",
            description = "Создание нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Пользователь создан",
                    content = {@Content(schema = @Schema(implementation = SimpleResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка создания пользователя",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
    })
    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(@ParameterObject @Valid CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new AlreadyExistsException("Пользователь с таким email уже существует: " + createUserRequest.getEmail());
        }

        securityService.register(createUserRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse("Пользователь создан: " + createUserRequest));
    }

    @Operation(summary = "Вход",
            description = "Возвращает данные пользователя, токен для аутентификации пользователя и токен для обновления токена аутентификации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Данные аутентификации",
                    content = {@Content(schema = @Schema(implementation = AuthResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка аутентификации пользователя",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(implementation = AuthErrorResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")})
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authUser(@ParameterObject @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(securityService.authenticateUser(loginRequest));
    }

    @Operation(summary = "Обновить токены",
            description = "Возвращает новый токен для аутентификации пользователя и токен для его обновления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Новые токены",
                    content = {@Content(schema = @Schema(implementation = RefreshTokenResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка обновления токенов",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "403",
                    description = "Введенные данные некорректны. Выполните вход снова",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")})
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@ParameterObject @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(securityService.refreshToken(request));
    }

    @Operation(summary = "Выход",
            description = "Выполняет выход, удаляет токен обновления")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Выход выполнен",
                    content = {@Content(schema = @Schema(implementation = SimpleResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(implementation = AuthErrorResponse.class), mediaType = "application/json")})
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logoutUser(@AuthenticationPrincipal UserDetails userDetails) {
        securityService.logout();

        return ResponseEntity.ok(new SimpleResponse("Выход: " + userDetails.getUsername()));
    }
}
