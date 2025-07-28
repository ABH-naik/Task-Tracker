package com.tasktracker.model.enums;


public enum RoleType {
    ADMIN,
    TASK_CREATOR,
    READ_ONLY_USER;

    public String toDisplayString() {
        return switch (this) {
            case ADMIN -> "Admin";
            case TASK_CREATOR -> "Task Creator";
            case READ_ONLY_USER -> "Read Only";
        };
    }

    public static RoleType fromString(String value) {
        return switch (value.toUpperCase()) {
            case "ADMIN" -> ADMIN;
            case "TASK_CREATOR" -> TASK_CREATOR;
            case "READ_ONLY", "READ_ONLY_USER" -> READ_ONLY_USER;
            default -> throw new IllegalArgumentException("Invalid role type: " + value);
        };
    }
}



