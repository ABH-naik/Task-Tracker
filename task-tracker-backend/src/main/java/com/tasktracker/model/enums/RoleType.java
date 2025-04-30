package com.tasktracker.model.enums;


    public enum RoleType {
        ADMIN,           // Full access (manage users, projects, tasks)
        TASK_CREATOR,    // Create/edit tasks
        READ_ONLY_USER   // View tasks + mark as complete
    }

