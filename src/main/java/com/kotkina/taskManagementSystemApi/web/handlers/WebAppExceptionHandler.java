package com.kotkina.taskManagementSystemApi.web.handlers;

import com.kotkina.taskManagementSystemApi.exceptions.*;
import com.kotkina.taskManagementSystemApi.web.models.responses.ErrorResponseBody;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class WebAppExceptionHandler {

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ErrorResponseBody> refreshTokenExceptionHandler(RefreshTokenException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.FORBIDDEN, ex, webRequest);
    }

    @ExceptionHandler(NoRightsForEntityChangeException.class)
    public ResponseEntity<ErrorResponseBody> noRightsForEntityChangeExceptionHandler(NoRightsForEntityChangeException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.FORBIDDEN, ex, webRequest);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponseBody> alreadyExistsExceptionHandler(AlreadyExistsException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseBody> illegalArgumentExceptionHandler(IllegalArgumentException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> entityNotFoundExceptionHandler(EntityNotFoundException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.NOT_FOUND, ex, webRequest);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> usernameNotFoundExceptionHandler(UsernameNotFoundException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.NOT_FOUND, ex, webRequest);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseBody> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest webRequest) {
        return buildResponse(ex.getStatusCode(), new Exception("Проверьте правильность запроса. <a href='/swagger-ui/index.html'>Перейти к документации</a>"), webRequest);
    }

    private ResponseEntity<ErrorResponseBody> buildResponse(HttpStatusCode httpStatus, Exception ex, WebRequest webRequest) {
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponseBody(ex.getMessage(), webRequest.getDescription(false)));
    }
}
