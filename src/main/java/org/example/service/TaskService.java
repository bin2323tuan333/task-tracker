package org.example.service;

import org.example.enums.TaskStatus;
import org.example.model.Task;
import org.example.repository.TaskRepository;

import java.util.List;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findTasks();
    }

    public List<Task> findByStatus(TaskStatus status) {
        return findAll().stream()
                .filter(task -> task.getStatus().equals(status))
                .toList();
    }

    public Task findById(int id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id không hợp lệ: " + id));
    }

    public void delete(int id) {
        Task task = findById(id);
        taskRepository.deleteById(id);
    }

    public void add(Task task) {
        taskRepository.save(task);
    }

    public void updateStatus(int id, TaskStatus status) {
        Task task = findById(id);
        task.updateStatus(status);
        taskRepository.save(task);
    }

    public void updateDescription(int id, String description) {
        Task task = findById(id);
        task.updateDescription(description);
        taskRepository.save(task);
    }
}
