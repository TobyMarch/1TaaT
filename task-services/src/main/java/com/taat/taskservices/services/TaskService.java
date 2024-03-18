package com.taat.taskservices.services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.model.UserTask;
import com.taat.taskservices.repository.TaskRepository;
import com.taat.taskservices.repository.UserTaskRepository;
import com.taat.taskservices.services.comparators.TaskDelayableComparator;
import com.taat.taskservices.services.comparators.TaskDueDateComparator;
import com.taat.taskservices.services.comparators.TaskPriorityComparator;
import com.taat.taskservices.services.comparators.TaskStartDateComparator;
import com.taat.taskservices.services.filters.TaskCurrentOrOverdueFilter;

import lombok.NonNull;
import reactor.core.publisher.Flux;

@Service
public class TaskService {

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    public Flux<Task> getPrioritizedTasks() {
        Sort priority = Sort.by(Sort.Direction.DESC, "priority");
        return taskRepository.findAll(priority);
    }

    public Flux<Task> createUpdateTasks(final List<Task> tasks) {
        try {
            return taskRepository.saveAll(tasks)
                    .doOnNext(savedTask -> {
                        userTaskRepository.findUserTaskByUserIdTaskId("", savedTask.getId())
                                .hasElement().subscribe(joinRecordExists -> {
                                    // Create a new entry linking the task to the user if none exists
                                    if (!joinRecordExists) {
                                        logger.info(
                                                String.format("Inserting record for task: %s", savedTask.getTitle()));
                                        UserTask joinEntry = new UserTask();
                                        joinEntry.setUserId("");
                                        joinEntry.setTaskId(savedTask.getId());
                                        userTaskRepository.insert(joinEntry).subscribe(insertedTask -> {
                                            logger.info(
                                                    String.format("Insertion result for task: %s",
                                                            insertedTask.toString()));
                                        });
                                    }
                                });
                    }).doOnComplete(() -> {
                        List<Task> unsortedTasks = new ArrayList<>();
                        List<UserTask> unsortedJoinRecords = new ArrayList<>();
                        List<UserTask> sortedJoinRecords = new ArrayList<>();
                        logger.info("Insertion complete, starting sort operation...");
                        // Select all tasks connected to the user
                        userTaskRepository.findByUserId("").doOnNext(joinRecord -> {
                            logger.info(String.format("Found join record: %s", joinRecord.toString()));
                            unsortedJoinRecords.add(joinRecord);
                            taskRepository.findById(joinRecord.getTaskId()).subscribe(taskRecord -> {
                                logger.info(String.format("Corresponding Task: %s", taskRecord.toString()));
                                unsortedTasks.add(taskRecord);
                            });
                        }).doOnComplete(() -> {
                            // Priority-Sort tasks then apply sorting values to UserTask records
                            double currentPriorityValue = unsortedTasks.size();
                            logger.info(String.format("Tasks to sort: %d", unsortedTasks.size()));
                            for (Task sortedTask : prioritySortTasks(unsortedTasks)) {
                                UserTask userTask = unsortedJoinRecords.stream()
                                        .filter(ut -> ut.getTaskId().equals(sortedTask.getId()))
                                        .collect(Collectors.toList())
                                        .get(0);
                                unsortedJoinRecords.remove(userTask);
                                userTask.setSortValue(currentPriorityValue--);
                                sortedJoinRecords.add(userTask);
                            }
                            logger.info(String.format("Saving %d records", sortedJoinRecords.size()));
                            // update UserTask records
                            userTaskRepository.saveAll(sortedJoinRecords);
                        }).subscribe();
                    });
        } catch (Exception e) {
            logger.error("Exception in save/update", e);
        }
        return null;
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
