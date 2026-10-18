package org.example.repository;

import org.example.model.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryTaskRepository implements TaskRepository {
    private final Map<Integer, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public List<Task> findTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Optional<Task> findById(int id) {
        Task task = tasks.get(id);
        return  Optional.ofNullable(task);
    }

    @Override
    public void save(Task task) {
        int id = task.getId();
        if (id <= 0) {
            id = idGenerator.getAndIncrement();
            Task newTask = new Task(
                    id,
                    task.getDescription(),
                    task.getStatus(),
                    task.getCreatedAt(),
                    task.getUpdatedAt()
            );
            tasks.put(id, newTask);
        } else {
            tasks.put(id, task);
        }
    }

    @Override
    public void deleteById(int id) {
        tasks.remove(id);
    }
}
