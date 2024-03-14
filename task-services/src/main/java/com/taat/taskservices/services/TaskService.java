package com.taat.taskservices.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.repository.TaskRepository;
import com.taat.taskservices.services.comparators.TaskDueDateComparator;

import lombok.NonNull;
import reactor.core.publisher.Flux;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Flux<Task> getPrioritizedTasks() {
        Sort priority = Sort.by(Sort.Direction.DESC, "priority");
        return taskRepository.findAll(priority);
    }

    public Flux<Task> createUpdateTasks(final List<Task> tasks) {
        return taskRepository.insert(tasks);
    }

    protected List<Task> prioritySortTasks(@NonNull final List<Task> taskList) {
        TaskDueDateComparator dueDateComparator = new TaskDueDateComparator();
        return taskList.stream().sorted(dueDateComparator).collect(Collectors.toList());
    }
}
