package com.taat.taskservices.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.repository.TaskRepository;

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

    // Delete Method
    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

    // Archive Method
    public Mono<Task> archiveTask(String id) {
        return taskRepository.findById(id)
                .flatMap(task -> {
                    task.setArchived(true);
                    return taskRepository.save(task);
                });
    }

}
