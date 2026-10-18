package org.example.service;

import org.example.enums.TaskStatus;
import org.example.model.Task;
import org.example.repository.InMemoryTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {
    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(new InMemoryTaskRepository());
    }

    @Test
    @DisplayName("Thêm task và lấy ra toàn bộ danh sách")
    void testAddAndFindAll() {
        service.add(new Task("Task 1"));
        service.add(new Task("Task 2"));

        List<Task> tasks = service.findAll();
        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Cập nhật trạng thái task thành công")
    void testUpdateStatus() {
        service.add(new Task("Task 1"));

        // Lấy lại task vừa lưu từ DB để có ID thực sự
        Task savedTask = service.findAll().get(0);

        // Dùng ID đó để cập nhật
        service.updateStatus(savedTask.getId(), TaskStatus.DONE);

        Task updatedTask = service.findAll().get(0);
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }

    @Test
    @DisplayName("Tìm kiếm task theo trạng thái")
    void testFindByStatus() {
        service.add(new Task("Task 1"));
        service.add(new Task("Task 2"));
        service.add(new Task("Task 3"));

        // Lấy lại danh sách đã lưu để có ID
        List<Task> savedTasks = service.findAll();
        Task savedT1 = savedTasks.get(0);
        Task savedT2 = savedTasks.get(1);

        // Dùng ID của đối tượng đã lưu để cập nhật
        service.updateStatus(savedT1.getId(), TaskStatus.DONE);
        service.updateStatus(savedT2.getId(), TaskStatus.IN_PROGRESS);

        assertEquals(1, service.findByStatus(TaskStatus.DONE).size());
        assertEquals(1, service.findByStatus(TaskStatus.IN_PROGRESS).size());
        assertEquals(1, service.findByStatus(TaskStatus.TODO).size());
    }

    @Test
    @DisplayName("Cập nhật mô tả task thành công")
    void testUpdateDescription() {
        service.add(new Task("Mô tả cũ"));

        // Lấy lại task vừa lưu để có ID
        Task savedTask = service.findAll().get(0);

        service.updateDescription(savedTask.getId(), "Mô tả mới");

        assertEquals("Mô tả mới", service.findAll().get(0).getDescription());
    }

    @Test
    @DisplayName("Xóa task thành công")
    void testDelete() {
        service.add(new Task("Task sắp bị xóa"));

        // Lấy lại task vừa lưu để có ID
        Task savedTask = service.findAll().get(0);

        service.delete(savedTask.getId());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    @DisplayName("Cập nhật task với ID không tồn tại phải ném ra IllegalArgumentException")
    void testUpdateNonExistentTask() {
        // Kiểm tra xem Service có ném ra IllegalArgumentException không
        assertThrows(IllegalArgumentException.class, () -> {
            service.updateDescription(999, "Mô tả ảo");
        });

        // Tương tự cho hàm delete
        assertThrows(IllegalArgumentException.class, () -> {
            service.delete(999);
        });
    }
}