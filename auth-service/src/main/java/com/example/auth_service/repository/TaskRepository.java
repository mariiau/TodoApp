package com.example.auth_service.repository;

import com.example.auth_service.model.Task;
import com.example.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUser(User user);
}