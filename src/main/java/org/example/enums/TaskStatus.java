package org.example.enums;

public enum TaskStatus {
    TODO("Todo"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canChangeTo(TaskStatus next) {
        boolean canChangeTo = false;
        switch (this) {
            case TODO -> canChangeTo = (next == IN_PROGRESS || next == DONE);
            case IN_PROGRESS ->  canChangeTo = (next == DONE);
        }
        return canChangeTo;
    }
}
