package com.taat.taskservices.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.repository.TaskRepository;

import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    TaskRepository taskRepo;

    @InjectMocks
    TaskService taskService;

    @Test
    public void testCreateUpdateTasks() {
        Task testTask = new Task("1", "testOwner", "Test Task", "A task for testing", null, null, null, 5,
                false);
        Flux<Task> taskFlux = Flux.just(testTask);
        Mockito.when(taskRepo.insert(Mockito.anyIterable())).thenReturn(taskFlux);

        List<Task> inputList = new ArrayList<>();
        inputList.add(testTask);
        taskService.createUpdateTasks(inputList);
        Mockito.verify(taskRepo, Mockito.times(1)).insert(Mockito.anyIterable());
    }

    @Test
    public void testGetPrioritizedTasks_Empty() {
        Flux<Task> taskFlux = Flux.just();
        Mockito.when(taskRepo.findAll(Mockito.any(Sort.class))).thenReturn(taskFlux);

        Flux<Task> results = taskService.getPrioritizedTasks();
        Assertions.assertNotNull(results);
        Mockito.verify(taskRepo, Mockito.times(1)).findAll(Mockito.any(Sort.class));
    }

    @Test
    public void testPrioritySortTasks_DueDate() {
        List<Task> unsortedList = getTestTasks();

        List<Task> sortedList = taskService.prioritySortTasks(unsortedList);
        Assertions.assertNotNull(sortedList);
        Assertions.assertEquals(unsortedList.size(), sortedList.size());
    }

    private List<Task> getTestTasks() {
        List<Task> taskList = new ArrayList<>();
        String currentDateString = LocalDateTime.now().toString().split("T")[0];
        String previousDateString = LocalDateTime.now().minusDays(1l).toString().split("T")[0];
        String futureDateString = LocalDateTime.now().plusDays(1l).toString().split("T")[0];
        taskList.add(
                new Task("1", "testOwner", "Test Task 1", "A task for testing", null, null,
                        currentDateString + "T09:45:00", 5, false));
        taskList.add(
                new Task("2", "testOwner", "Test Task 2", "A task for testing", null, null,
                        currentDateString + "T09:30:00", 5, false));
        taskList.add(
                new Task("3", "testOwner", "Test Task 3", "A task with a null due date", null, null,
                        null, 5, false));
        taskList.add(
                new Task("4", "testOwner", "Test Task 4", "A task that was due yesterday", null, null,
                        previousDateString + "T09:15:00", 5, false));
        taskList.add(
                new Task("4", "testOwner", "Test Task 5", "A task that is due tomorrow", null, null,
                        futureDateString + "T14:15:00", 5, false));

        return taskList;
    }

}
