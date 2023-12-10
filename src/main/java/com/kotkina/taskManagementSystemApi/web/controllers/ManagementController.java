package com.kotkina.taskManagementSystemApi.web.controllers;

import com.kotkina.taskManagementSystemApi.entities.Comment;
import com.kotkina.taskManagementSystemApi.exceptions.NoRightsForEntityChangeException;
import com.kotkina.taskManagementSystemApi.services.CommentService;
import com.kotkina.taskManagementSystemApi.entities.Task;
import com.kotkina.taskManagementSystemApi.services.TaskService;
import com.kotkina.taskManagementSystemApi.entities.TaskStatus;
import com.kotkina.taskManagementSystemApi.entities.User;
import com.kotkina.taskManagementSystemApi.services.UserService;
import com.kotkina.taskManagementSystemApi.utils.mapper.CommentMapper;
import com.kotkina.taskManagementSystemApi.utils.mapper.TaskMapper;
import com.kotkina.taskManagementSystemApi.web.models.requests.ListPage;
import com.kotkina.taskManagementSystemApi.web.models.responses.*;
import com.kotkina.taskManagementSystemApi.web.models.requests.UpsertCommentRequest;
import com.kotkina.taskManagementSystemApi.web.models.requests.UpsertTaskRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
@Tag(name = "Менеджер задач")
public class ManagementController {

    private final TaskService taskService;
    private final UserService userService;
    private final CommentService commentService;

    @Operation(summary = "Задачи пользователя (автор)",
            description = "Возвращает список задач пользователя с указанным email или текущего пользователя (без указания email), автором которых он является.")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Задачи пользователя",
                    content = {@Content(schema = @Schema(implementation = TaskResponseList.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "204",
                    description = "У пользователя нет задач",
                    content = {@Content()}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка при поиске задач пользователя",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(implementation = AuthErrorResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")})
    })
    @GetMapping({"/tasks/author", "/tasks/author/{email}"})
    public ResponseEntity<TaskResponseList> getTasksByAuthor(
            @PathVariable(value = "email", required = false) String email,
            @Parameter(required = false) @ParameterObject ListPage page) {
        User user = getUser(email);

        List<Task> tasksByAuthor = taskService.getTasksByAuthor(user, page);
        if (tasksByAuthor.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(
                TaskMapper.taskListToTaskResponseList(tasksByAuthor)
        );
    }

    @Operation(summary = "Задачи пользователя (исполнитель)",
            description = "Возвращает список задач пользователя с указанным email или текущего пользователя (без указания email), где он является исполнителем")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Задачи пользователя",
                    content = {@Content(schema = @Schema(implementation = TaskResponseList.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "204",
                    description = "У пользователя нет задач",
                    content = {@Content()}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка при поиске задач пользователя",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")})
    })
    @GetMapping({"/tasks/executor", "/tasks/executor/{email}"})
    @SecurityRequirement(name = "Bearer Token")
    public ResponseEntity<TaskResponseList> getTasksByExecutor(
            @PathVariable(value = "email", required = false) String email,
            @ParameterObject ListPage page) {
        User user = getUser(email);

        List<Task> tasksByExecutor = taskService.getTasksByExecutor(user, page);
        if (tasksByExecutor.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(
                TaskMapper.taskListToTaskResponseList(tasksByExecutor)
        );
    }

    @Operation(summary = "Новая задача",
            description = "Создание новой задачи, автором которой будет текущий пользователь")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Задача создана",
                    content = {@Content(schema = @Schema(implementation = TaskResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка сохранения задачи",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
    })
    @PostMapping("/task/new")
    public ResponseEntity<TaskResponse> addTask(@ParameterObject @Valid UpsertTaskRequest request) {
        User author = getUser();
        User executor = StringUtils.isBlank(request.getExecutorEmail()) ? null : getUser(request.getExecutorEmail());

        Task createdTask = taskService.save(TaskMapper.requestToTask(request, author, executor));
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskMapper.taskToResponse(createdTask));
    }

    @Operation(summary = "Новый комментарий к задаче",
            description = "Добавление комментария к задаче")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Комментарий создан",
                    content = {@Content(schema = @Schema(implementation = TaskResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка сохранения комментария",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(implementation = AuthErrorResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Задача не найдена",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")})
    })
    @PostMapping("/comment/new/{taskId}")
    public ResponseEntity<CommentResponse> addComment(@PathVariable long taskId,
                                                      @ParameterObject @Valid UpsertCommentRequest request) {
        User author = getUser();

        Task task = taskService.getTaskById(taskId);

        Comment comment = commentService.save(CommentMapper.requestToComment(request, task, author));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentMapper.commentToResponse(comment));
    }

    @Operation(summary = "Обновление задачи",
            description = "Обновить задачу полностью может только ее автор")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Задача обновлена",
                    content = {@Content(schema = @Schema(implementation = TaskResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка обновления задачи",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(implementation = AuthErrorResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "403",
                    description = "Нет прав на изменение задачи другого автора",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Задача не найдена",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")})
    })
    @PutMapping("/task/update/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable long id,
                                                   @ParameterObject @Valid UpsertTaskRequest request) {
        Task oldTask = taskService.getTaskById(id);

        User author = getUser();
        User executor = StringUtils.isBlank(request.getExecutorEmail()) ? null : getUser(request.getExecutorEmail());

        checkTaskForUpdateAndDeleteByUser(oldTask, author);

        Task updatedTask = taskService.update(TaskMapper.requestToTask(oldTask, request, author, executor));
        return ResponseEntity.ok(TaskMapper.taskToResponse(updatedTask));
    }

    @Operation(summary = "Добавление/обновление исполнителя задачи",
            description = "Изменить исполнителя задачи может только ее автор")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Задача обновлена",
                    content = {@Content(schema = @Schema(implementation = TaskResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка обновления задачи",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(implementation = AuthErrorResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "403",
                    description = "Нет прав на изменение задачи другого автора",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Задача не найдена",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")})
    })
    @PutMapping("/task/update/executor")
    public ResponseEntity<TaskResponse> updateTaskExecutor(@RequestParam("id") long id,
                                                           @RequestParam("executorEmail") String executorEmail) {
        Task oldTask = taskService.getTaskById(id);

        User author = getUser();
        checkTaskForUpdateAndDeleteByUser(oldTask, author);

        User executor = getUser(executorEmail);

        oldTask.setExecutor(executor);
        Task updatedTask = taskService.update(oldTask);
        return ResponseEntity.ok(TaskMapper.taskToResponse(updatedTask));
    }

    @Operation(summary = "Обновление статуса задачи",
            description = "Изменить статус задачи может только ее автор или исполнитель")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Задача обновлена",
                    content = {@Content(schema = @Schema(implementation = TaskResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка обновления задачи",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(implementation = AuthErrorResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "403",
                    description = "Нет прав на изменение задачи другого автора/исполнителя",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Задача не найдена",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")})
    })
    @PutMapping("/task/update/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@RequestParam("id") long id,
                                                         @RequestParam("status") TaskStatus status) {

        Task oldTask = taskService.getTaskById(id);

        User author = getUser();
        checkTaskForStatusUpdateByUser(oldTask, author);

        oldTask.setStatus(status);
        Task updatedTask = taskService.update(oldTask);
        return ResponseEntity.ok(TaskMapper.taskToResponse(updatedTask));
    }

    @Operation(summary = "Удаление задачи",
            description = "Удалить задачу может только ее автор")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Задача удалена",
                    content = {@Content(schema = @Schema(implementation = SimpleResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "Ошибка удаления задачи",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = {@Content(schema = @Schema(implementation = AuthErrorResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "403",
                    description = "Нет прав на удаление задачи другого автора",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "Задача не найдена",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseBody.class), mediaType = "application/json")})
    })
    @DeleteMapping("/task/delete/{id}")
    public ResponseEntity<SimpleResponse> deleteTask(@PathVariable long id) {
        Task oldTask = taskService.getTaskById(id);

        checkTaskForUpdateAndDeleteByUser(oldTask, getUser());

        taskService.delete(oldTask);
        return ResponseEntity.status(HttpStatus.OK).body(new SimpleResponse("Задача и все ее комментарии удалены, id: " + id));
    }

    private User getUser() {
        return userService.getCurrentUser().getUser();
    }

    private User getUser(String email) {
        if (StringUtils.isBlank(email)) {
            return getUser();
        }

        return userService.getUserByEmail(email);
    }

    private void checkTaskForUpdateAndDeleteByUser(Task task, User user) {
        if (task.getAuthor().getId() != user.getId()) {
            throw new NoRightsForEntityChangeException("Только автор задачи может изменять и удалять ее");
        }
    }

    private void checkTaskForStatusUpdateByUser(Task task, User user) {
        if ((task.getExecutor() != null && task.getExecutor().getId() != user.getId() || task.getExecutor() == null) && task.getAuthor().getId() != user.getId()) {
            throw new NoRightsForEntityChangeException("Только автор и исполнитель задачи может изменять ее статус");
        }
    }
}
