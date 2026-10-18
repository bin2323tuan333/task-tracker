package org.example.model;

import org.example.enums.TaskStatus;

import java.time.Instant;

public class Task {
    private final int id;
    private String description;
    private TaskStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public Task(int id, String description, TaskStatus status, Instant createdAt, Instant updatedAt) {
        checkId(id);
        checkDescription(description);
        checkDate(createdAt,updatedAt);
        this.id = id;
        this.createdAt = createdAt;
        this.description = description;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public Task(String description) {
        checkDescription(description);
        this.id = 0;
        this.description = description;
        this.status = TaskStatus.TODO;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateDescription(String description) {
        checkDescription(description);
        this.description = description;
        touch();
    }

    public void updateStatus(TaskStatus status) {
        if (!this.status.canChangeTo(status)) {
            throw  new IllegalArgumentException("Cannot change " + this.status + " to " + status);
        }
        this.status = status;
        touch();
    }

    public boolean isValidTask() {
        return id > 0 &&
                description != null &&
                !description.isBlank();
    }

    private void checkId(int id) {
        if (id <= 0)
            throw new IllegalArgumentException("Id must be greater than 0");
    }

    private void checkDate(Instant created, Instant updated) {
        if (updated.isBefore(created))
            throw new IllegalArgumentException("Created date must be after updated date");
    }

    private void checkDescription(String description) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description must be not null or blank");
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
}
