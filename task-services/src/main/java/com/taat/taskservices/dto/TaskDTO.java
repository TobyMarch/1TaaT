package com.taat.taskservices.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.taat.taskservices.model.Task;

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

    public Task dtoToEntity() {
        Task taskEntity = new Task();
        taskEntity.setId(id);
        taskEntity.setOwner(owner);
        taskEntity.setTitle(title);
        taskEntity.setDescription(description);
        taskEntity.setCreatedDate(createdDate);
        taskEntity.setStartDate(startDate);
        taskEntity.setDueDate(dueDate);
        taskEntity.setPriority(priority);
        taskEntity.setDuration(duration);
        taskEntity.setDelayable(isDelayable);
        taskEntity.setArchived(archived);
        taskEntity.setSubTasks(subTasks.stream().map(TaskDTO::getId).collect(Collectors.toList()));

        return taskEntity;
    }

    public static TaskDTO entityToDTO(Task taskEntity, List<TaskDTO> subTasks) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(taskEntity.getId());
        taskDTO.setOwner(taskEntity.getOwner());
        taskDTO.setTitle(taskEntity.getTitle());
        taskDTO.setDescription(taskEntity.getDescription());
        taskDTO.setCreatedDate(taskEntity.getCreatedDate());
        taskDTO.setStartDate(taskEntity.getStartDate());
        taskDTO.setDueDate(taskEntity.getDueDate());
        taskDTO.setPriority(taskEntity.getPriority());
        taskDTO.setDuration(taskEntity.getDuration());
        taskDTO.setDelayable(taskEntity.isDelayable());
        taskDTO.setArchived(taskEntity.isArchived());
        taskDTO.setSubTasks(subTasks);

        return taskDTO;
    }
}
