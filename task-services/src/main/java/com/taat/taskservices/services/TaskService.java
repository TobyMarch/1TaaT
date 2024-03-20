package com.taat.taskservices.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.repository.TaskRepository;
import com.taat.taskservices.services.comparators.TaskDelayableComparator;
import com.taat.taskservices.services.comparators.TaskDueDateComparator;
import com.taat.taskservices.services.comparators.TaskPriorityComparator;
import com.taat.taskservices.services.comparators.TaskStartDateComparator;
import com.taat.taskservices.services.filters.TaskCurrentOrOverdueFilter;

import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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

    protected List<Task> prioritySortTasks(@NonNull final List<Task> taskList) {
        TaskCurrentOrOverdueFilter currentOrOverdueFilter = new TaskCurrentOrOverdueFilter();
        TaskDueDateComparator dueDateComparator = new TaskDueDateComparator();
        TaskDelayableComparator delayableComparator = new TaskDelayableComparator();
        TaskPriorityComparator priorityComparator = new TaskPriorityComparator();
        TaskStartDateComparator startDateComparator = new TaskStartDateComparator();

        // filter by date to apply different priority sorting logic
        List<Task> currentOrOverdueTasks = taskList.stream().filter(currentOrOverdueFilter)
                .sorted(delayableComparator.thenComparing(dueDateComparator).thenComparing(priorityComparator))
                .collect(Collectors.toList());
        List<Task> futureTasks = taskList.stream().filter(Predicate.not(currentOrOverdueFilter))
                .sorted(dueDateComparator.thenComparing(startDateComparator).thenComparing(priorityComparator))
                .collect(Collectors.toList());

        List<Task> returnList = new ArrayList<>();
        returnList.addAll(currentOrOverdueTasks);
        returnList.addAll(futureTasks);

        return returnList;
    }
}
