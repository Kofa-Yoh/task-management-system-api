package com.kotkina.taskManagementSystemApi.repositories;

import com.kotkina.taskManagementSystemApi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByHash(String hash);

    Boolean existsByEmail(String email);
}
