package com.example.todoApp.repository;

import com.example.todoApp.model.Task;
import com.example.todoApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByUser(User user);
}