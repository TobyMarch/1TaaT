package com.taat.taskservices.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.RecurrenceSet;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recurrenceset.FastForwarded;
import org.dmfs.rfc5545.recurrenceset.Merged;
import org.dmfs.rfc5545.recurrenceset.OfRule;
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
import com.taat.taskservices.services.comparators.TaskDurationComparator;
import com.taat.taskservices.services.comparators.TaskPriorityComparator;
import com.taat.taskservices.services.comparators.TaskStartDateComparator;
import com.taat.taskservices.services.comparators.UserTaskSortComparator;
import com.taat.taskservices.services.filters.TaskCurrentOrOverdueFilter;
import com.taat.taskservices.utils.Constants;

import lombok.NonNull;

@Service
public class ImperativeTaskService {
    private final Logger logger = LoggerFactory.getLogger(ImperativeTaskService.class);

    @Autowired
    ImperativeTaskRepository taskRepo;

    @Autowired
    ImperativeUserTaskRepository userTaskRepo;

    public List<Task> getPrioritizedTasks(String owner) {
        // Sort priority = Sort.by(Sort.Direction.DESC, "priority");
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
            return taskRepo.existsById(taskId) && !subTaskIds.contains(taskId);
        }).map(taskId -> {
            return taskRepo.buildHierarchicalRecordById(taskId);
        }).collect(Collectors.toList());
        logger.info("{} Task results for user: {}", taskDTOs.size(), userId);

        // Pagination on this List
        int start = Math.toIntExact(sortingByPriorityPageable.getOffset());
        int end = Math.min((start + sortingByPriorityPageable.getPageSize()), taskDTOs.size());
        List<TaskDTO> pageContent = (start <= end) ? taskDTOs.subList(start, end) : Collections.emptyList();

        return new PageImpl<>(pageContent, sortingByPriorityPageable, taskDTOs.size());
    }

    public TaskDTO getTopTask(final String userId) {
        UserTask currentTopTask = userTaskRepo.findTopUserTask(userId);
        // Ensure that prioritization has run at least once today
        Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
        if (currentTopTask.getLastSorted() == null || currentTopTask.getLastSorted().isBefore(startOfDay)) {
            this.runPrioritization(userId);
        }
        return TaskDTO.entityToDTO(userTaskRepo.findTopTaskByUserTaskSort(userId, startOfDay), Collections.emptyList());
    }

    public Page<TaskDTO> getArchivedTasks(final String userId, Pageable pageable) {
        List<TaskDTO> content = userTaskRepo.findArchivedTasksByUserIdPaginated(userId,
                (pageable.getPageNumber() * pageable.getPageSize()), pageable.getPageSize()).stream()
                .map(taskEntity -> {
                    return TaskDTO.entityToDTO(taskEntity, null);
                }).collect(Collectors.toList());
        Long userTaskCount = userTaskRepo.getArchivedTaskCountByUserId(userId);

        return new PageImpl<>(content, pageable, userTaskCount);
    }

    public List<TaskDTO> createUpdateTasks(final List<TaskDTO> tasks, String owner) {
        try {
            // create/update Tasks with parent-to-subTask relationships preserved
            List<Task> savedTasks = batchSaveTasks(tasks, owner);
            // create/update UserTask join records for all created/updated Tasks
            this.saveUserTasks(savedTasks, owner);

            logger.info("Insertion complete, starting sort operation...");
            this.runPrioritization(owner);
            return savedTasks.stream().map(task -> {
                return TaskDTO.entityToDTO(task, Collections.emptyList());
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Exception in save/update", e);
            return null;
        }
    }

    private List<Task> batchSaveTasks(List<TaskDTO> taskDTOs, String owner) {
        List<Task> taskEntities = new ArrayList<>();
        for (TaskDTO dto : taskDTOs) {
            Task inputTaskEntity = dto.dtoToEntity();
            inputTaskEntity.setOwner(owner);
            inputTaskEntity.setSubTasks(null);
            if (inputTaskEntity.getCreatedDate() == null) {
                inputTaskEntity.setCreatedDate(Instant.now());
            }

            if (dto.getSubTasks() != null && !dto.getSubTasks().isEmpty()) {
                List<String> subTaskIds = new ArrayList<>();
                for (TaskDTO subTaskDTO : dto.getSubTasks()) {
                    Task inputSubTaskEntity = subTaskDTO.dtoToEntity();
                    if (inputSubTaskEntity.getCreatedDate() == null) {
                        inputSubTaskEntity.setCreatedDate(Instant.now());
                    }

                    Task savedEntity = taskRepo.save(inputSubTaskEntity);
                    if (savedEntity != null) {
                        taskEntities.add(savedEntity);
                        subTaskIds.add(savedEntity.getId());
                    }
                }
                inputTaskEntity.setSubTasks(subTaskIds);
            }
            Task savedTaskEntity = taskRepo.save(inputTaskEntity);
            if (savedTaskEntity != null) {
                taskEntities.add(inputTaskEntity);
            }
        }
        return taskEntities;
    }

    private void saveUserTasks(List<Task> taskEntities, String userId) {
        for (Task inputTask : taskEntities) {
            UserTask userTaskRecord = userTaskRepo.findByUserIdTaskId(userId,
                    inputTask.getId());
            // Create a new db entry linking the task to the user if none exists
            if (userTaskRecord == null) {
                logger.info(String.format("Inserting record for task: %s",
                        inputTask.getTitle()));
                UserTask newUserTask = createUserTask(inputTask, userId);
                userTaskRepo.insert(newUserTask);
            } else {
                userTaskRecord.setArchived(inputTask.isArchived());
                userTaskRepo.save(userTaskRecord);
            }
        }
    }

    private void runPrioritization(String userId) {
        List<Task> unsortedTasks = new ArrayList<>();
        List<UserTask> sortedJoinRecords = new ArrayList<>();

        // Select all active tasks connected to the user
        List<UserTask> joinList = userTaskRepo.findByUserId(userId).stream()
                .filter(Predicate.not(UserTask::isArchived))
                .collect(Collectors.toList());
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
                userTask.setLastSorted(Instant.now());
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
    }

    public boolean deleteById(String id, String owner) {
        if (taskRepo.existsByOwnerAndId(owner, id)) {
            taskRepo.deleteById(id);
            userTaskRepo.deleteByTaskId(id);
            return true;
        } else {
            return false;
        }
    }

    public Task archiveTask(String id, String owner) {
        if (taskRepo.existsByOwnerAndId(owner, id)) {
            Task existingTask = taskRepo.findById(id).get();
            existingTask.setArchived(true);
            Task updatedTask = taskRepo.save(existingTask);

            // If task has recurrence schedule, generate next instance
            if (updatedTask.getRecurrence() != null && !updatedTask.getRecurrence().isEmpty()
                    && (updatedTask.getStartDate() != null || updatedTask.getDueDate() != null)) {
                this.createNextRecurringTask(updatedTask, owner);
            }

            // Archive tasks for all assigned users
            List<UserTask> existingUserTasks = userTaskRepo.findByTaskId(id).stream().map(userTask -> {
                userTask.setArchived(true);
                return userTask;
            }).collect(Collectors.toList());
            userTaskRepo.saveAll(existingUserTasks);

            return updatedTask;
        }
        return null;
    }

    private void createNextRecurringTask(Task task, String owner) {
        try {
            List<String> recurrenceParameters = task.getRecurrence();
            if (recurrenceParameters != null && !recurrenceParameters.isEmpty()
                    && (task.getStartDate() != null || task.getDueDate() != null)) {
                // Create new task with new start and due dates
                task.setId(null);
                task.setArchived(false);
                if (task.getStartDate() != null) {
                    task.setStartDate(generateNextDate(recurrenceParameters, task.getStartDate()));
                }
                if (task.getDueDate() != null) {
                    task.setDueDate(generateNextDate(recurrenceParameters, task.getDueDate()));
                }

                List<TaskDTO> nextTaskList = Collections
                        .singletonList(TaskDTO.entityToDTO(task, Collections.emptyList()));
                this.createUpdateTasks(nextTaskList, owner);
            }
        } catch (Exception e) {
            logger.error("Exception when generating recurring task instance", e);
        }
    }

    private Instant generateNextDate(List<String> recurrenceParameters, Instant previousDate)
            throws InvalidRecurrenceRuleException {
        List<RecurrenceSet> mergedList = new ArrayList<>();
        Pattern rRulePattern = Pattern.compile(Constants.RRULE_REGEX);
        Optional<String> ruleString = recurrenceParameters.stream().filter(rRulePattern.asPredicate()).findFirst();
        if (ruleString.isPresent() && previousDate != null) {
            String rRule = ruleString.get().split(":")[1];
            DateTime firstInstance = new DateTime(previousDate.toEpochMilli());
            mergedList.add(new OfRule(new RecurrenceRule(rRule), firstInstance));
        }
        // Assume that the user always wants their next task to occur in the future
        RecurrenceSet occurrences = new FastForwarded(DateTime.now(), new Merged(mergedList));
        DateTime next = occurrences.iterator().next();

        return Instant.ofEpochMilli(next.getTimestamp());
    }

    public Optional<UserTask> skipTask(String taskId, String userId) throws NullPointerException {
        UserTask userTaskRecord = userTaskRepo.findByUserIdTaskId(userId, taskId);
        if (userTaskRecord != null) {
            // By default, skip task for remainder of the day
            Instant skipDate = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
            userTaskRecord.setSkipUntil(skipDate);
            UserTask savedRecord = userTaskRepo.save(userTaskRecord);
            return Optional.of(savedRecord);
        } else {
            return Optional.empty();
        }
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
        TaskDurationComparator taskDurationComparator = new TaskDurationComparator();

        // filter by date to apply different priority sorting logic
        List<Task> currentOrOverdueTasks = taskList.stream().filter(currentOrOverdueFilter)
                .sorted(delayableComparator.thenComparing(dueDateComparator).thenComparing(priorityComparator).thenComparing(taskDurationComparator))
                .collect(Collectors.toList());
        List<Task> futureTasks = taskList.stream().filter(Predicate.not(currentOrOverdueFilter))
                .sorted(dueDateComparator.thenComparing(startDateComparator).thenComparing(priorityComparator).thenComparing(taskDurationComparator))
                .collect(Collectors.toList());

        List<Task> returnList = new ArrayList<>();
        returnList.addAll(currentOrOverdueTasks);
        returnList.addAll(futureTasks);

        return returnList;
    }

}
