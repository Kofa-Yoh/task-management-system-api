package com.kotkina.taskManagementSystemApi.utils.mapper;

import com.kotkina.taskManagementSystemApi.entities.Task;
import com.kotkina.taskManagementSystemApi.entities.User;
import com.kotkina.taskManagementSystemApi.web.models.responses.TaskResponseList;
import com.kotkina.taskManagementSystemApi.web.models.responses.TaskResponse;
import com.kotkina.taskManagementSystemApi.web.models.requests.UpsertTaskRequest;
import io.micrometer.common.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    public static Task requestToTask(UpsertTaskRequest request, User author, User executor) {
        if (request == null || author == null) return null;

        Task task = new Task();
        if (StringUtils.isBlank(request.getTitle())) {
            task.setTitle("Задача");
        } else {
            task.setTitle(request.getTitle());
        }
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setAuthor(author);
        task.setExecutor(executor);

        return task;
    }

    public static Task requestToTask(Task oldTask, UpsertTaskRequest request, User author, User executor) {
        if (oldTask == null || request == null || author == null) return null;

        if (!StringUtils.isBlank(request.getTitle())) {
            oldTask.setTitle(request.getTitle());
        }
        if (!StringUtils.isBlank(request.getDescription())) {
            oldTask.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            oldTask.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            oldTask.setPriority(request.getPriority());
        }
        if (author != null) {
            oldTask.setAuthor(author);
        }
        if (executor != null) {
            oldTask.setExecutor(executor);
        }

        return oldTask;
    }

    public static TaskResponse taskToResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setStatus(task.getStatus() == null ? "" : task.getStatus().getMessage());
        taskResponse.setPriority(task.getPriority() == null ? "" : task.getPriority().getTitle());
        taskResponse.setAuthor(UserMapper.userToResponse(task.getAuthor()));
        taskResponse.setExecutor(UserMapper.userToResponse(task.getExecutor()));
        taskResponse.setComments(CommentMapper.commentListToCommentResponseList(task.getComments()));

        return taskResponse;
    }

    public static List<TaskResponse> listToResponseList(List<Task> tasks) {
        return tasks.stream()
                .map(TaskMapper::taskToResponse)
                .collect(Collectors.toList());
    }

    public static TaskResponseList taskListToTaskResponseList(List<Task> tasks) {
        if (tasks == null) return new TaskResponseList();
        if (tasks.size() == 0) return new TaskResponseList();

        TaskResponseList taskListResponse = new TaskResponseList();
        taskListResponse.setTasks(listToResponseList(tasks));

        return taskListResponse;
    }
}
