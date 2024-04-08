package com.taat.taskservices.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.taat.taskservices.dto.TaskDTO;
import com.taat.taskservices.model.Task;
import com.taat.taskservices.model.UserTask;
import com.taat.taskservices.repository.imperative.ImperativeTaskRepository;
import com.taat.taskservices.repository.imperative.ImperativeUserTaskRepository;
import com.taat.taskservices.services.comparators.TaskDelayableComparator;
import com.taat.taskservices.services.comparators.TaskDueDateComparator;
import com.taat.taskservices.services.comparators.TaskPriorityComparator;
import com.taat.taskservices.services.comparators.TaskStartDateComparator;
import com.taat.taskservices.services.comparators.UserTaskSortComparator;
import com.taat.taskservices.services.filters.TaskCurrentOrOverdueFilter;

import lombok.NonNull;

@Service
public class ImperativeTaskService {
    private final Logger logger = LoggerFactory.getLogger(ImperativeTaskService.class);

    @Autowired
    ImperativeTaskRepository taskRepo;

    @Autowired
    ImperativeUserTaskRepository userTaskRepo;

    public List<Task> getPrioritizedTasks(String owner) {
        Sort priority = Sort.by(Sort.Direction.DESC, "priority");
        return taskRepo.findAllByOwner(owner);
    }

    public Page<TaskDTO> getPaginatedTasks(String userId, Pageable pageable) {
        logger.info("Querying Tasks for user: {}", userId);
        Pageable sortingByPriorityPageable = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "priority"));

        // Get all task IDs for a specific user, in descending order
        List<String> activeTaskIDs = userTaskRepo.findByUserId(userId).stream()
                .filter(Predicate.not(UserTask::isArchived))
                .sorted(new UserTaskSortComparator())
                .map(UserTask::getTaskId)
                .collect(Collectors.toList());
        // Filter out tasks that are subtasks
        List<Task> subTasks = userTaskRepo.findSubTasksByUserId(userId);
        List<String> subTaskIds = subTasks.stream().map(Task::getId).collect(Collectors.toList());

        // Convert tasks to a taskDTO (Insert subtasks into parents)
        List<TaskDTO> taskDTOs = activeTaskIDs.stream().filter(taskId -> {
            return !subTaskIds.contains(taskId);
        }).map(taskId -> {
            return taskRepo.buildHierarchicalRecordById(taskId);
        }).collect(Collectors.toList());
        logger.info("{} Task results for user: {}", taskDTOs.size(), userId);

        // Pagination on this List
        int start = (int) sortingByPriorityPageable.getOffset();
        int end = Math.min((start + sortingByPriorityPageable.getPageSize()), taskDTOs.size());
        List<TaskDTO> pageContent = taskDTOs.subList(start, end);

        return new PageImpl<>(pageContent, sortingByPriorityPageable, taskDTOs.size());
    }

    public long getTaskCount() {
        return taskRepo.count();
    }

    public Task getTopTask(final String userId) {
        return userTaskRepo.findTopTaskByUserTaskSort(userId);
    }

    public List<Task> createUpdateTasks(final List<Task> tasks, String owner) {
        try {
            List<Task> savedTasks = new ArrayList<>();
            for (Task inputTask : tasks) {
                Task savedTaskRecord = taskRepo.save(inputTask);
                if (savedTaskRecord != null) {
                    savedTasks.add(savedTaskRecord);
                    UserTask userTaskRecord = userTaskRepo.findByUserIdTaskId(owner,
                            savedTaskRecord.getId());
                    // Create a new db entry linking the task to the user if none exists
                    if (userTaskRecord == null) {
                        logger.info(String.format("Inserting record for task: %s",
                                savedTaskRecord.getTitle()));
                        UserTask newUserTask = createUserTask(savedTaskRecord, owner);
                        userTaskRepo.insert(newUserTask);
                    } else {
                        userTaskRecord.setArchived(savedTaskRecord.isArchived());
                        userTaskRepo.save(userTaskRecord);
                    }
                }
            }

            List<Task> unsortedTasks = new ArrayList<>();
            List<UserTask> sortedJoinRecords = new ArrayList<>();
            // Select all tasks connected to the user
            logger.info("Insertion complete, starting sort operation...");
            List<UserTask> joinList = userTaskRepo.findByUserId(owner);
            for (UserTask joinRecord : joinList) {
                logger.debug(String.format("Found join record: %s", joinRecord.toString()));
                Optional<Task> taskRecordOptional = taskRepo.findById(joinRecord.getTaskId());
                if (taskRecordOptional.isPresent()) {
                    logger.debug(String.format("Corresponding Task: %s",
                            taskRecordOptional.toString()));
                    unsortedTasks.add(taskRecordOptional.get());
                }
            }
            // Priority-Sort tasks then apply sorting values to UserTask records
            double currentPriorityValue = unsortedTasks.size();
            logger.info(String.format("Tasks to sort: %d", unsortedTasks.size()));
            for (Task sortedTask : prioritySortTasks(unsortedTasks)) {
                Optional<UserTask> userTaskOptional = joinList.stream()
                        .filter(ut -> ut.getTaskId().equals(sortedTask.getId())).findFirst();
                if (userTaskOptional.isPresent()) {
                    UserTask userTask = userTaskOptional.get();
                    userTask.setSortValue(currentPriorityValue--);
                    sortedJoinRecords.add(userTask);
                }
            }
            logger.info(String.format("Saving %d records", sortedJoinRecords.size()));
            // update UserTask records
            List<UserTask> output = userTaskRepo.saveAll(sortedJoinRecords);
            if (output != null) {
                for (UserTask userTask : output) {
                    logger.info(String.format("Saved join record: %s", userTask.toString()));
                }
            }
            return savedTasks;
        } catch (Exception e) {
            logger.error("Exception in save/update", e);
            return null;
        }
    }

    public boolean deleteById(String id) {
        if (taskRepo.existsById(id)) {
            taskRepo.deleteById(id);
            userTaskRepo.deleteByTaskId(id);
            return true;
        } else {
            return false;
        }
    }

    public Task archiveTask(String id) {
        if (taskRepo.existsById(id)) {
            Task existingTask = taskRepo.findById(id).get();
            existingTask.setArchived(true);
            Task updatedTask = taskRepo.save(existingTask);

            for (UserTask existingUserTask : userTaskRepo.findByTaskId(id)) {
                existingUserTask.setArchived(true);
                userTaskRepo.save(existingUserTask);
            }

            return updatedTask;
        }
        return null;
    }

    private UserTask createUserTask(final Task task, final String userId) {
        UserTask joinEntry = new UserTask();
        joinEntry.setUserId(userId);
        joinEntry.setTaskId(task.getId());
        joinEntry.setArchived(task.isArchived());

        return joinEntry;
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
