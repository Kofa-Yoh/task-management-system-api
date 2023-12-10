package com.kotkina.taskManagementSystemApi.services;

import com.kotkina.taskManagementSystemApi.exceptions.EntityNotFoundException;
import com.kotkina.taskManagementSystemApi.entities.Task;
import com.kotkina.taskManagementSystemApi.repositories.TaskRepository;
import com.kotkina.taskManagementSystemApi.entities.User;
import com.kotkina.taskManagementSystemApi.repositories.UserRepository;
import com.kotkina.taskManagementSystemApi.web.models.requests.ListPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с таким id не найдена: " + id));
    }

    public List<Task> getTasksByAuthor(User user, ListPage page) {
        Pageable nextPage = PageRequest.of(page.getPage(), page.getSize());
        return taskRepository.findTasksByAuthor(user, nextPage)
                .getContent();
    }

    public List<Task> getTasksByExecutor(User user, ListPage page) {
        Pageable nextPage = PageRequest.of(page.getPage(), page.getSize());
        return taskRepository.findTasksByExecutor(user, nextPage)
                .getContent();
    }

    public Task save(Task task) {
        checkUserExists(task.getAuthor());
        if (task.getExecutor() != null) {
            checkUserExists(task.getExecutor());
        }

        return taskRepository.save(task);
    }

    public Task update(Task task) {
        checkTaskExists(task.getId());
        checkUserExists(task.getAuthor());
        if (task.getExecutor() != null) {
            checkUserExists(task.getExecutor());
        }

        return taskRepository.save(task);
    }

    public void delete(Task task) {
        checkTaskExists(task.getId());

        taskRepository.delete(task);
    }

    private void checkUserExists(User user) {
        if (user == null || !userRepository.existsById(user.getId())) {
            throw new EntityNotFoundException("Ошибка сохранения/изменения задачи: Не удалось найти пользователя с таким id");
        }
    }

    private void checkTaskExists(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Ошибка сохранения/изменения задачи: Не удалось найти задачу с таким id");
        }
    }
}
