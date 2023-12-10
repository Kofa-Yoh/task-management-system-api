package com.kotkina.taskManagementSystemApi.repositories;

import com.kotkina.taskManagementSystemApi.entities.Task;
import com.kotkina.taskManagementSystemApi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsById(Long id);

    Page<Task> findTasksByAuthor(User user, Pageable nextPage);

    Page<Task> findTasksByExecutor(User user, Pageable nextPage);
}
