package org.example.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonFileTaskRepository implements TaskRepository {
    private final File dataFile;
    private final ObjectMapper mapper;
    private final Map<Integer, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);


    public JsonFileTaskRepository(File dataFile) {
        this.dataFile = dataFile;
        this.mapper = new ObjectMapper();
        loadData(); // Tải dữ liệu khi khởi tạo
    }

    private void loadData() {
        if (!dataFile.exists()) { return;}

        try {
            List<Task> taskList = mapper.readValue(dataFile, new TypeReference<List<Task>>() {});
            for (Task task : taskList) {
                tasks.put(task.getId(), task);
                if (task.getId() >= idGenerator.get()) {
                    idGenerator.set(task.getId() + 1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file dữ liệu: " + e.getMessage(), e);
        }
    }

    private void saveData() {
        try {
            mapper.writeValue(dataFile, new ArrayList<>(tasks.values()));
        } catch (IOException e) {
            throw new RuntimeException("Không thể ghi file dữ liệu: " + e.getMessage(), e);
        }
    }


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
