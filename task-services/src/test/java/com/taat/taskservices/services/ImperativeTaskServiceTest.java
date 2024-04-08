package com.taat.taskservices.services;

import java.time.LocalDateTime;
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
import com.taat.taskservices.repository.imperative.ImperativeTaskRepository;
import com.taat.taskservices.repository.imperative.ImperativeUserTaskRepository;
import com.taat.taskservices.utils.Duration;

@ExtendWith(MockitoExtension.class)
public class ImperativeTaskServiceTest {
    @Mock
    ImperativeTaskRepository impTaskRepo;

    @Mock
    ImperativeUserTaskRepository impUserTaskRepo;

    @InjectMocks
    ImperativeTaskService taskService;

    @Test
    public void testGetPrioritizedTasks_Empty() {
        List<Task> taskFlux = Collections.emptyList();
        Mockito.when(impTaskRepo.findAllByOwner(Mockito.anyString())).thenReturn(taskFlux);

        List<Task> results = taskService.getPrioritizedTasks("");
        Assertions.assertNotNull(results);
        Mockito.verify(impTaskRepo, Mockito.times(1)).findAllByOwner(Mockito.anyString());
    }

    @Test
    public void testGetPaginatedTasks() {
        String currentDateString = LocalDateTime.now().toString().split("T")[0];
        List<Task> taskList = getTestTasks();
        Task parentTask = taskList.get(0);
        parentTask.setSubTasks(Collections.singletonList("6"));
        taskList.set(0, parentTask);
        taskList.add(new Task("6", "testOwner", "Test Task 6", "A sub-task", null, null,
                currentDateString + "T09:30:00", 5, Duration.M.toString(), false, false,
                Collections.emptyList()));

        Mockito.when(impUserTaskRepo.findByUserId("testUser")).thenReturn(getTestTaskJoinEntries(taskList));
        Mockito.when(impUserTaskRepo.findSubTasksByUserId("testUser"))
                .thenReturn(Collections.singletonList(taskList.get(taskList.size() - 1)));
        Mockito.when(impTaskRepo.buildHierarchicalRecordById(Mockito.anyString())).thenReturn(new TaskDTO());

        Pageable testPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "priority"));
        Page<TaskDTO> results = taskService.getPaginatedTasks("testUser", testPageable);
        Assertions.assertNotNull(results);
        Mockito.verify(impTaskRepo, Mockito.times(taskList.size() - 1))
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
        List<UserTask> userTaskList = getTestTaskJoinEntries(taskList);

        Mockito.when(impTaskRepo.save(Mockito.any(Task.class))).thenReturn(taskList.get(0),
                taskList.get(1),
                taskList.get(2), taskList.get(3), taskList.get(4));
        Mockito.when(impUserTaskRepo.findByUserId(Mockito.anyString())).thenReturn(userTaskList);
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("1"))).thenReturn(Optional.of(taskList.get(0)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("2"))).thenReturn(Optional.of(taskList.get(1)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("3"))).thenReturn(Optional.of(taskList.get(2)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("4"))).thenReturn(Optional.of(taskList.get(3)));
        Mockito.when(impTaskRepo.findById(ArgumentMatchers.eq("5"))).thenReturn(Optional.of(taskList.get(4)));

        taskService.createUpdateTasks(taskList, "abcd");
        Mockito.verify(impTaskRepo, Mockito.times(5)).save(Mockito.any(Task.class));
        Mockito.verify(impUserTaskRepo,
                Mockito.times(5)).insert(Mockito.any(UserTask.class));
        Mockito.verify(impUserTaskRepo,
                Mockito.times(1)).findByUserId(Mockito.anyString());
        Mockito.verify(impTaskRepo, Mockito.times(5)).findById(Mockito.anyString());
        Mockito.verify(impUserTaskRepo,
                Mockito.times(1)).saveAll(Mockito.anyIterable());
    }

    @Test
    public void testDeleteById() {
        String taskId = "1";
        Mockito.when(impTaskRepo.existsById(taskId)).thenReturn(true);

        boolean result = taskService.deleteById(taskId);
        Assertions.assertTrue(result);

        Mockito.verify(impTaskRepo, Mockito.times(1)).deleteById(taskId);
        Mockito.verify(impUserTaskRepo, Mockito.times(1)).deleteByTaskId(taskId);
    }

    @Test
    public void testArchiveTask() {
        String taskId = "1";
        Task testTask = new Task(taskId, "testOwner", "Test Task", "A task for testing", null, null, null, 5,
                Duration.S.toString(), false,
                false, Collections.emptyList());

        Mockito.when(impTaskRepo.existsById(taskId)).thenReturn(true);
        Mockito.when(impTaskRepo.findById(taskId)).thenReturn(Optional.of(testTask));
        Mockito.when(impTaskRepo.save(testTask)).thenReturn(testTask);
        Mockito.when(impUserTaskRepo.findByTaskId(taskId))
                .thenReturn(Collections.singletonList(getTestTaskJoinEntries(getTestTasks()).get(0)));
        Mockito.when(impUserTaskRepo.save(Mockito.any(UserTask.class)))
                .thenReturn(getTestTaskJoinEntries(getTestTasks()).get(0));

        Task result = taskService.archiveTask(taskId);
        Assertions.assertNotNull(result);
        Mockito.verify(impTaskRepo, Mockito.times(1)).findById(taskId);
        Mockito.verify(impTaskRepo, Mockito.times(1)).save(testTask);
        Mockito.verify(impUserTaskRepo, Mockito.times(1)).findByTaskId(taskId);
        Mockito.verify(impUserTaskRepo, Mockito.times(1)).save(Mockito.any(UserTask.class));
    }

    private List<Task> getTestTasks() {
        List<Task> taskList = new ArrayList<>();
        String currentDateString = LocalDateTime.now().toString().split("T")[0];
        String previousDateString = LocalDateTime.now().minusDays(1l).toString().split("T")[0];
        String futureDateString = LocalDateTime.now().plusDays(1l).toString().split("T")[0];
        taskList.add(
                new Task("1", "testOwner", "Test Task 1", "A task for testing", null, null,
                        currentDateString + "T09:45:00", 5, Duration.M.toString(), false, false,
                        Collections.singletonList("6")));
        taskList.add(
                new Task("2", "testOwner", "Test Task 2", "A task for testing", null, null,
                        currentDateString + "T09:30:00", 5, Duration.M.toString(), false, false,
                        Collections.emptyList()));
        taskList.add(
                new Task("3", "testOwner", "Test Task 3", "A task with a null due date", null, null,
                        null, 5, Duration.M.toString(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("4", "testOwner", "Test Task 4", "A task that was due yesterday", null, null,
                        previousDateString + "T09:15:00", 5, Duration.M.toString(), false, false,
                        Collections.emptyList()));
        taskList.add(
                new Task("5", "testOwner", "Test Task 5", "A task that is due tomorrow", null, null,
                        futureDateString + "T14:15:00", 5, Duration.M.toString(), false, false,
                        Collections.emptyList()));

        return taskList;
    }

    private List<UserTask> getTestTaskJoinEntries(List<Task> taskList) {
        int index = 0;
        List<UserTask> joinEntries = new ArrayList<>();
        for (Task task : taskList) {
            joinEntries.add(new UserTask(Integer.toString(index++), "", task.getId(), null, null, null, false));
        }
        return joinEntries;
    }
}
