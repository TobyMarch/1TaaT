package com.taat.taskservices.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.taat.taskservices.dto.TaskDTO;
import com.taat.taskservices.model.Task;
import com.taat.taskservices.model.UserTask;
import com.taat.taskservices.repository.TaskRepository;
import com.taat.taskservices.repository.UserTaskRepository;
import com.taat.taskservices.utils.Duration;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    TaskRepository impTaskRepo;

    @Mock
    UserTaskRepository impUserTaskRepo;

    @InjectMocks
    TaskService taskService;

    @Test
    public void testGetTopTasks() {
        List<Task> testTasks = getTestTasks();
        UserTask testUserTask = getTestTaskJoinEntries(testTasks, "testUser").get(0);
        testUserTask.setLastSorted(Instant.now());
        Mockito.when(impUserTaskRepo.findTopUserTask(Mockito.anyString())).thenReturn(testUserTask);
        Mockito.when(impUserTaskRepo.findTopTaskByUserTaskSort(Mockito.anyString(), Mockito.any(Instant.class)))
                .thenReturn(testTasks.get(0));

        Optional<TaskDTO> topTask = taskService.getTopTask("testUser");
        Assertions.assertNotNull(topTask);
        Mockito.verify(impUserTaskRepo, Mockito.times(0)).saveAll(Mockito.anyIterable());
    }

    @Test
    public void testGetTopTasks_ForcePrioritization() {
        List<Task> testTasks = getTestTasks();
        UserTask testUserTask = getTestTaskJoinEntries(testTasks, "testUser").get(0);
        testUserTask.setLastSorted(Instant.now().minus(1, ChronoUnit.DAYS));
        Mockito.when(impUserTaskRepo.findTopUserTask(Mockito.anyString())).thenReturn(testUserTask);
        Mockito.when(impUserTaskRepo.findTopTaskByUserTaskSort(Mockito.anyString(), Mockito.any(Instant.class)))
                .thenReturn(testTasks.get(0));

        Optional<TaskDTO> topTask = taskService.getTopTask("testUser");
        Assertions.assertNotNull(topTask);
        Mockito.verify(impUserTaskRepo, Mockito.times(1)).saveAll(Mockito.anyIterable());
    }

    @Test
    public void testGetPaginatedTasks() {
        List<Task> taskList = getTestTasks();
        Mockito.when(impUserTaskRepo.findUserTasksByUserIdSortParams(Mockito.eq("testUser"), Mockito.anyString(),
                Mockito.anyInt()))
                .thenReturn(getTestTaskJoinEntries(taskList, "testUser"));
        Mockito.when(impUserTaskRepo.findSubTasksByUserId("testUser"))
                .thenReturn(new ArrayList<>(List.of(taskList.get(0), taskList.get(3), taskList.get(4))));
        Mockito.when(impTaskRepo.existsById(Mockito.anyString())).thenReturn(true);
        Mockito.when(impTaskRepo.buildHierarchicalRecordById(Mockito.anyString())).thenReturn(new TaskDTO());

        Pageable testPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "priority"));
        Page<TaskDTO> results = taskService.getPaginatedTasks("testUser", testPageable);
        Assertions.assertNotNull(results);
        Mockito.verify(impTaskRepo, Mockito.times(taskList.size() - 3))
                .buildHierarchicalRecordById(Mockito.anyString());
    }

    @Test
    public void testGetPaginatedTasks_InvalidProperty() {
        List<Task> taskList = getTestTasks();
        Mockito.when(impUserTaskRepo.findUserTasksByUserIdSortParams(Mockito.eq("testUser"), Mockito.anyString(),
                Mockito.anyInt()))
                .thenReturn(getTestTaskJoinEntries(taskList, "testUser"));
        Mockito.when(impUserTaskRepo.findSubTasksByUserId("testUser"))
                .thenReturn(new ArrayList<>(List.of(taskList.get(0), taskList.get(3), taskList.get(4))));
        Mockito.when(impTaskRepo.existsById(Mockito.anyString())).thenReturn(true);
        Mockito.when(impTaskRepo.buildHierarchicalRecordById(Mockito.anyString())).thenReturn(new TaskDTO());

        Pageable testPageable = PageRequest.of(0, 5, Sort.by(Sort.DEFAULT_DIRECTION, "anInvalidProperty"));
        Page<TaskDTO> results = taskService.getPaginatedTasks("testUser", testPageable);
        Assertions.assertNotNull(results);
        Mockito.verify(impTaskRepo, Mockito.times(taskList.size() - 3))
                .buildHierarchicalRecordById(Mockito.anyString());
    }

    @Test
    public void testGetArchivedTasks() {
        List<Task> taskList = getTestTasks();
        Mockito.when(impUserTaskRepo.findArchivedTasksByUserIdPaginated("", 0, 5)).thenReturn(taskList);
        Mockito.when(impUserTaskRepo.getArchivedTaskCountByUserId("")).thenReturn(Long.valueOf(taskList.size()));

        Pageable testPageable = PageRequest.of(0, 5, Sort.unsorted());
        Page<TaskDTO> results = taskService.getArchivedTasks("", testPageable);
        Assertions.assertNotNull(results);
        Assertions.assertEquals(taskList.size(), results.getTotalElements());

        List<TaskDTO> pageContent = results.getContent();
        Assertions.assertNotNull(pageContent);
        Assertions.assertEquals(taskList.size(), pageContent.size());

    }

    @Test
    public void testImperativeCreateUpdateTasks() {
        List<Task> taskList = getTestTasks();
        List<UserTask> userTaskList = getTestTaskJoinEntries(taskList, "testUser");

        Mockito.when(impTaskRepo.save(Mockito.any(Task.class))).thenReturn(taskList.get(0), taskList.get(1),
                taskList.get(2), taskList.get(3), taskList.get(4), taskList.get(5),
                taskList.get(6), taskList.get(7));
        Mockito.when(impUserTaskRepo.findByUserId(Mockito.anyString())).thenReturn(userTaskList);
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("6"))).thenReturn(Optional.of(taskList.get(0)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("1"))).thenReturn(Optional.of(taskList.get(1)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("2"))).thenReturn(Optional.of(taskList.get(2)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("7"))).thenReturn(Optional.of(taskList.get(3)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("8"))).thenReturn(Optional.of(taskList.get(4)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("3"))).thenReturn(Optional.of(taskList.get(5)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("4"))).thenReturn(Optional.of(taskList.get(6)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("5"))).thenReturn(Optional.of(taskList.get(7)));

        List<TaskDTO> taskDTOList = getTestTaskDTOs();
        taskService.createUpdateTasks(taskDTOList, "abcd");
        Mockito.verify(impTaskRepo, Mockito.times(8)).save(Mockito.any(Task.class));
        Mockito.verify(impUserTaskRepo,
                Mockito.times(8)).insert(Mockito.any(UserTask.class));
        Mockito.verify(impUserTaskRepo,
                Mockito.times(1)).findByUserId(Mockito.anyString());
        Mockito.verify(impTaskRepo, Mockito.times(8)).findById(Mockito.anyString());
        Mockito.verify(impUserTaskRepo,
                Mockito.times(1)).saveAll(Mockito.anyIterable());
    }

    @Test
    public void testDeleteById() {
        String taskId = "1";
        Task testTask = new Task(taskId, "ext-" + taskId, "testUser", "Test Task 1", "A task for testing", null, null,
                null, 5, Duration.M.toString(), Collections.emptyList(), false, false, null);

        Mockito.when(impTaskRepo.existsByOwnerAndId(Mockito.anyString(), Mockito.eq(taskId))).thenReturn(true);
        Mockito.when(impTaskRepo.findById(taskId)).thenReturn(Optional.of(testTask));

        boolean result = taskService.deleteById(taskId, "owner");
        Assertions.assertTrue(result);

        Mockito.verify(impTaskRepo, Mockito.times(1)).deleteById(taskId);
        Mockito.verify(impUserTaskRepo, Mockito.times(1)).deleteByTaskId(taskId);
    }

    @Test
    public void testDeleteById_SubTasks() {
        String taskId = "1";
        Task testTask = new Task(taskId, "ext-" + taskId, "testUser", "Test Task 1", "A task for testing", null, null,
                null, 5, Duration.M.toString(), Collections.emptyList(), false, false, List.of("2", "3"));

        Mockito.when(impTaskRepo.existsByOwnerAndId(Mockito.anyString(), Mockito.eq(taskId))).thenReturn(true);
        Mockito.when(impTaskRepo.findById(taskId)).thenReturn(Optional.of(testTask));

        boolean result = taskService.deleteById(taskId, "owner");
        Assertions.assertTrue(result);

        Mockito.verify(impTaskRepo, Mockito.times(3)).deleteById(Mockito.anyString());
        Mockito.verify(impUserTaskRepo, Mockito.times(3)).deleteByTaskId(Mockito.anyString());
    }

    @Test
    public void testDeleteById_NotFound() {
        String taskId = "1";
        Mockito.when(impTaskRepo.existsByOwnerAndId(Mockito.anyString(), Mockito.eq(taskId))).thenReturn(false);

        boolean result = taskService.deleteById(taskId, "owner");
        Assertions.assertFalse(result);

        Mockito.verify(impTaskRepo, Mockito.times(0)).deleteById(Mockito.anyString());
        Mockito.verify(impUserTaskRepo, Mockito.times(0)).deleteByTaskId(Mockito.anyString());
    }

    @Test
    public void testArchiveTask() {
        String taskId = "1";
        Instant startDate = Instant.now().minus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);
        Instant dueDate = Instant.now().minus(30, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.HOURS);
        List<String> recurrence = new ArrayList<>(List.of("RRULE:FREQ=WEEKLY;INTERVAL=1;BYDAY=SU,TH"));
        Task testTask = new Task(taskId, "ext-" + taskId, "testUser", "Test Task", "A task for testing", null,
                startDate, dueDate, 5,
                Duration.S.toString(), recurrence, false, false, null);
        Task spyTestTask = Mockito.spy(testTask);
        Task nextTask = new Task(String.valueOf(Integer.parseInt(taskId) + 1), "ext-" + taskId, "testUser", "Test Task",
                "A task for testing", null, null, null, 5, Duration.S.toString(), recurrence, false, false, null);

        Mockito.when(impTaskRepo.existsByOwnerAndId(Mockito.anyString(), Mockito.eq(taskId))).thenReturn(true);
        Mockito.when(impTaskRepo.findById(taskId)).thenReturn(Optional.of(spyTestTask));
        Mockito.when(impTaskRepo.save(Mockito.any(Task.class))).thenReturn(spyTestTask);
        Mockito.when(impTaskRepo.existsByOwnerAndExternalId(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(false);

        List<UserTask> joinEntries = getTestTaskJoinEntries(List.of(testTask, nextTask), "testUser");
        Mockito.when(impUserTaskRepo.findByTaskIds(List.of(taskId)))
                .thenReturn(Collections.singletonList(joinEntries.get(0)));
        Mockito.when(impUserTaskRepo.insert(Mockito.any(UserTask.class))).thenReturn(joinEntries.get(1));

        TaskDTO result = taskService.archiveTask(taskId, "testUser");
        Assertions.assertNotNull(result);
        Mockito.verify(spyTestTask).setArchived(true);
        Mockito.verify(impTaskRepo, Mockito.times(1)).findById(taskId);
        Mockito.verify(impTaskRepo, Mockito.times(2)).save(Mockito.any(Task.class));
        Mockito.verify(impUserTaskRepo, Mockito.times(1)).findByTaskIds(List.of(taskId));
        Mockito.verify(impUserTaskRepo, Mockito.times(2)).saveAll(Mockito.anyIterable());
    }

    @Test
    public void testSkipTask() {
        String taskId = "1";
        String userId = "testUser";
        UserTask userTask = new UserTask("2", userId, taskId, null, null, null, false);
        UserTask spyUserTask = Mockito.spy(userTask);
        Mockito.when(impUserTaskRepo.findByUserIdTaskId(userId, taskId)).thenReturn(spyUserTask);
        Mockito.when(impUserTaskRepo.save(Mockito.any(UserTask.class))).thenReturn(spyUserTask);

        Optional<UserTask> result = taskService.skipTask(taskId, "testUser");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isPresent());

        Mockito.verify(impUserTaskRepo, Mockito.times(1)).save(Mockito.any(UserTask.class));
        Instant skipDate = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Mockito.verify(spyUserTask).setSkipUntil(skipDate);
    }

    @Test
    public void testSkipTask_NotFound() {
        String taskId = "1";
        String userId = "testUser";
        Mockito.when(impUserTaskRepo.findByUserIdTaskId(userId, taskId)).thenReturn(null);

        Optional<UserTask> result = taskService.skipTask(taskId, "testUser");
        Assertions.assertFalse(result.isPresent());
        Mockito.verify(impUserTaskRepo, Mockito.times(0)).save(Mockito.any(UserTask.class));
    }

    @Test
    public void testSkipTask_ThrowException() {
        String taskId = "1";
        String userId = "testUser";
        UserTask userTask = new UserTask("2", userId, taskId, null, null, null, false);
        UserTask spyUserTask = Mockito.spy(userTask);
        Mockito.when(impUserTaskRepo.findByUserIdTaskId(userId, taskId)).thenReturn(spyUserTask);
        Mockito.when(impUserTaskRepo.save(Mockito.any(UserTask.class))).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> {
            taskService.skipTask(taskId, "testUser");
        });
    }

    private List<Task> getTestTasks() {
        List<Task> taskList = new ArrayList<>();
        Instant currentDateMidnight = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant previousDateMidnight = currentDateMidnight.minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant futureDateMidnight = currentDateMidnight.plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        taskList.add(
                new Task("6", "ext-6", "testUser", "Test Task 6", "A sub-task of Task 1", null, null,
                        currentDateMidnight.plus(9, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("1", "ext-1", "testUser", "Test Task 1", "A task for testing", null, null,
                        currentDateMidnight.plus(9, ChronoUnit.HOURS).plus(45, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.singletonList("6")));
        taskList.add(
                new Task("2", "ext-2", "testUser", "Test Task 2", "A task for testing", null, null,
                        currentDateMidnight.plus(9, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("7", "ext-7", "testUser", "Test Task 7", "A sub-task of Task 3", null, null,
                        null, 4, Duration.M.toString(), Collections.emptyList(), false, false,
                        Collections.emptyList()));
        taskList.add(
                new Task("8", "ext-8", "testUser", "Test Task 7", "A sub-task of Task 3", null, null,
                        null, 3, Duration.S.toString(), Collections.emptyList(), false, false,
                        Collections.emptyList()));
        taskList.add(
                new Task("3", "ext-3", "testUser", "Test Task 3", "A task with a null due date", null, null,
                        null, 5, Duration.L.toString(), Collections.emptyList(), false, false,
                        Collections.emptyList()));
        taskList.add(
                new Task("4", "ext-4", "testUser", "Test Task 4", "A task that was due yesterday", null, null,
                        previousDateMidnight.plus(9, ChronoUnit.HOURS).plus(15, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("5", "ext-5", "testUser", "Test Task 5", "A task that is due tomorrow", null, null,
                        futureDateMidnight.plus(14, ChronoUnit.HOURS).plus(15, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()));

        return taskList;
    }

    private List<TaskDTO> getTestTaskDTOs() {
        List<TaskDTO> taskList = new ArrayList<>();
        Instant currentDateMidnight = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant previousDateMidnight = currentDateMidnight.minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant futureDateMidnight = currentDateMidnight.plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);

        List<TaskDTO> task1SubTasks = new ArrayList<>(
                List.of(new TaskDTO("6", "ext-6", "testUser", "Test Task 6", "A sub-task", null, null,
                        currentDateMidnight.plus(9, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList())));
        taskList.add(
                new TaskDTO("1", "ext-1", "testUser", "Test Task 1", "A task for testing", null, null,
                        currentDateMidnight.plus(9, ChronoUnit.HOURS).plus(45, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, task1SubTasks));
        taskList.add(
                new TaskDTO("2", "ext-2", "testUser", "Test Task 2", "A task for testing", null, null,
                        currentDateMidnight.plus(9, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()));
        List<TaskDTO> task3SubTasks = new ArrayList<>(
                List.of(new TaskDTO("7", "ext-7", "testUser", "Test Task 7", "A sub-task of Task 3", null, null,
                        null, 4, Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()),
                        new TaskDTO("8", "ext-8", "testUser", "Test Task 7", "A sub-task of Task 3", null, null,
                                null, 3, Duration.S.toString(), Collections.emptyList(), false, false,
                                Collections.emptyList())));
        taskList.add(
                new TaskDTO("3", "ext-3", "testUser", "Test Task 3", "A task with a null due date", null, null,
                        null, 5, Duration.L.toString(), Collections.emptyList(), false, false, task3SubTasks));
        taskList.add(
                new TaskDTO("4", "ext-4", "testUser", "Test Task 4", "A task that was due yesterday", null, null,
                        previousDateMidnight.plus(9, ChronoUnit.HOURS).plus(15, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new TaskDTO("5", "ext-5", "testUser", "Test Task 5", "A task that is due tomorrow", null, null,
                        futureDateMidnight.plus(14, ChronoUnit.HOURS).plus(15, ChronoUnit.MINUTES), 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()));

        return taskList;
    }

    private List<UserTask> getTestTaskJoinEntries(List<Task> taskList, String userId) {
        int index = 0;
        List<UserTask> joinEntries = new ArrayList<>();
        for (Task task : taskList) {
            joinEntries.add(new UserTask(Integer.toString(index++), userId, task.getId(), null, null, null, false));
        }
        return joinEntries;
    }
}
