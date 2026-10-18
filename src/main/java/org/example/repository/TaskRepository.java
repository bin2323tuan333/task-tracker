package org.example.repository;

import org.example.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findTasks();
    Optional<Task> findById(int id);
    void save(Task task);
    void deleteById(int id);
}
