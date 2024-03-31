package com.taat.taskservices.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private String id;

    private String owner;

    private String title;

    private String description;

    private String createdDate;

    private String startDate;

    private String dueDate;

    private int priority;

    private String duration;

    private boolean isDelayable;

    private boolean archived;

    private List<TaskDTO> subTasks;
}
