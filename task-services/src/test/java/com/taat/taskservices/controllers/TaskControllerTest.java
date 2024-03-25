package com.taat.taskservices.controllers;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.services.TaskService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    TaskService taskService;

    @InjectMocks
    TaskController taskController;

    @Test
    public void testGetTasks() {
        ResponseEntity<Flux<Task>> results = taskController.getTasks();
        Assertions.assertNotNull(results);
    }

    @Test
    public void testGetTopTask() {
        Flux<Task> taskFlux = Flux.fromIterable(getTestTasks());
        Mockito.when(taskService.getPrioritizedTasks()).thenReturn(taskFlux);
        ResponseEntity<Mono<Task>> results = taskController.getTopTask();
        Assertions.assertNotNull(results);
    }

    @Test
    public void testGetPaginatedTasks() {
        Flux<Task> taskFlux = Flux.fromIterable(getTestTasks());
        Mono<Long> taskCount = Mono.just(5l);
        Mockito.when(taskService.getPaginatedTasks(Mockito.any(Pageable.class))).thenReturn(taskFlux);
        Mockito.when(taskService.getTaskCount()).thenReturn(taskCount);

        Pageable testPageable = PageRequest.of(0, 5, Sort.unsorted());
        ResponseEntity<Mono<Page<Task>>> results = taskController.getPaginatedTasks(testPageable);
        Assertions.assertNotNull(results);
    }

    private List<Task> getTestTasks() {
        List<Task> taskList = new ArrayList<>();
        String currentDateString = LocalDateTime.now().toString().split("T")[0];
        String previousDateString = LocalDateTime.now().minusDays(1l).toString().split("T")[0];
        String futureDateString = LocalDateTime.now().plusDays(1l).toString().split("T")[0];
        taskList.add(
                new Task("1", "testOwner", "Test Task 1", "A task for testing", null, null,
                        currentDateString + "T09:45:00", 5, false, false));
        taskList.add(
                new Task("2", "testOwner", "Test Task 2", "A task for testing", null, null,
                        currentDateString + "T09:30:00", 5, false, false));
        taskList.add(
                new Task("3", "testOwner", "Test Task 3", "A task with a null due date", null, null,
                        null, 5, false, false));
        taskList.add(
                new Task("4", "testOwner", "Test Task 4", "A task that was due yesterday", null, null,
                        previousDateString + "T09:15:00", 5, false, false));
        taskList.add(
                new Task("5", "testOwner", "Test Task 5", "A task that is due tomorrow", null, null,
                        futureDateString + "T14:15:00", 5, false, false));

        return taskList;
    }

}
