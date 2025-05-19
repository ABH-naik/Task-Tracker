package com.tasktracker.dto.request;

import lombok.Data;

@Data
public class TaskQueryRequest {
    private Long userId;
    private Long projectId;
}
