package com.kotkina.taskManagementSystemApi.utils.mapper;

import com.kotkina.taskManagementSystemApi.entities.Comment;
import com.kotkina.taskManagementSystemApi.entities.Task;
import com.kotkina.taskManagementSystemApi.entities.User;
import com.kotkina.taskManagementSystemApi.web.models.responses.CommentResponseList;
import com.kotkina.taskManagementSystemApi.web.models.responses.CommentResponse;
import com.kotkina.taskManagementSystemApi.web.models.requests.UpsertCommentRequest;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentResponse commentToResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setDescription(comment.getDescription());
        commentResponse.setUser(UserMapper.userToResponse(comment.getUser()));

        return commentResponse;
    }

    public static List<CommentResponse> listToResponseList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::commentToResponse)
                .collect(Collectors.toList());
    }

    public static CommentResponseList commentListToCommentResponseList(List<Comment> comments) {
        if (comments == null) return new CommentResponseList();
        if (comments.size() == 0) return new CommentResponseList();

        CommentResponseList commentListResponse = new CommentResponseList();
        commentListResponse.setComments(listToResponseList(comments));

        return commentListResponse;
    }

    public static Comment requestToComment(UpsertCommentRequest request, Task task, User author) {
        if (request == null || task == null || author == null) return null;

        Comment comment = new Comment();
        comment.setDescription(request.getDescription());
        comment.setTask(task);
        comment.setUser(author);

        return comment;
    }
}
