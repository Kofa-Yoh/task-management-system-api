package com.kotkina.taskManagementSystemApi.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kotkina.taskManagementSystemApi.web.models.responses.AuthErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        AuthErrorResponse body = new AuthErrorResponse();
        body.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        body.setError(authException.getMessage());
        body.setMessage("Пользователь не авторизован. Проверьте данные пользователя и выполните вход");
        body.setPath(request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
