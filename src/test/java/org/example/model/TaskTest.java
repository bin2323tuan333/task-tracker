package org.example.model;

import org.example.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    @Test
    @DisplayName("Tạo task mới phải có ID, trạng thái TODO và thời gian hiện tại")
    void testCreateNewTask() {
        Task task = new Task("Học Java");

        assertNotNull(task.getId(), "ID không được null");
        assertEquals("Học Java", task.getDescription());
        assertEquals(TaskStatus.TODO, task.getStatus(), "Trạng thái mặc định phải là TODO");
        assertNotNull(task.getUpdatedAt(), "Thời gian cập nhật không được null");
        assertTrue(task.getUpdatedAt().isBefore(Instant.now().plusSeconds(1)), "Thời gian phải là hiện tại");
    }
}
