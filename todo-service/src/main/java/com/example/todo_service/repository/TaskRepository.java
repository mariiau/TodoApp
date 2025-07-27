package com.example.todo_service.repository;

import com.example.todo_service.model.Task;
import com.example.todo_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUserId(Long userId);
}