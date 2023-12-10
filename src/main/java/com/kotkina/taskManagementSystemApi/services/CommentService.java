package com.kotkina.taskManagementSystemApi.services;

import com.kotkina.taskManagementSystemApi.exceptions.EntityNotFoundException;
import com.kotkina.taskManagementSystemApi.repositories.CommentRepository;
import com.kotkina.taskManagementSystemApi.entities.Comment;
import com.kotkina.taskManagementSystemApi.repositories.TaskRepository;
import com.kotkina.taskManagementSystemApi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public Comment save(Comment comment) {
        if (taskRepository.existsById(comment.getTask().getId())) {
            throw new EntityNotFoundException("Ошибка сохранения комментария: Не удалось найди задачу с таким id");
        }
        if (userRepository.existsById(comment.getUser().getId())) {
            throw new EntityNotFoundException("Ошибка сохранения комментария: Не удалось найди пользователя");
        }
        return commentRepository.save(comment);
    }
}
