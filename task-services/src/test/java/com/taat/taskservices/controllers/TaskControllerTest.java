package com.taat.taskservices.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.taat.taskservices.dto.TaskDTO;
import com.taat.taskservices.model.Task;
// import com.taat.taskservices.services.TaskService;
import com.taat.taskservices.services.ImperativeTaskService;
import com.taat.taskservices.utils.Duration;

// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    ImperativeTaskService taskService;

    @InjectMocks
    TaskController taskController;

    // @Test
    // public void testGetTasks() {
    // ResponseEntity<Flux<Task>> results = taskController.getTasks();
    // Assertions.assertNotNull(results);
    // }

    // @Test
    // public void testGetTopTask() {
    // Mono<Task> taskFlux = Mono.just(getTestTasks().get(0));
    // Mockito.when(taskService.getTopTask(Mockito.anyString())).thenReturn(taskFlux);
    // ResponseEntity<Mono<Task>> results = taskController.getTopTask();
    // Assertions.assertNotNull(results);
    // }

    @Test
    public void testGetTopTask_Imperative() {
        Task taskFlux = getTestTasks().get(0).dtoToEntity();
        Mockito.when(taskService.getTopTask(Mockito.anyString())).thenReturn(taskFlux);
        ResponseEntity<Task> results = taskController.getTopTask();
        Assertions.assertNotNull(results);
    }

    // @Test
    // public void testGetPaginatedTasks() {
    // Flux<Task> taskFlux = Flux.fromIterable(getTestTasks());
    // Mono<Long> taskCount = Mono.just(5l);
    // Mockito.when(taskService.getPaginatedTasks(Mockito.any(Pageable.class))).thenReturn(taskFlux);
    // Mockito.when(taskService.getTaskCount()).thenReturn(taskCount);

    // Pageable testPageable = PageRequest.of(0, 5, Sort.unsorted());
    // ResponseEntity<Mono<Page<Task>>> results =
    // taskController.getPaginatedTasks(testPageable);
    // Assertions.assertNotNull(results);
    // }

    @Test
    public void testGetPaginatedTasks_Imperative() {
        List<TaskDTO> taskFlux = getTestTasks();
        Mockito.when(taskService.getPaginatedTasks(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<TaskDTO>(taskFlux));

        Pageable testPageable = PageRequest.of(0, 5, Sort.unsorted());
        ResponseEntity<Page<TaskDTO>> results = taskController.getPaginatedTasks(testPageable);
        Assertions.assertNotNull(results);
    }

    @Test
    public void testGetPaginatedTasks_Exception() {
        Mockito.when(taskService.getPaginatedTasks(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenThrow(new NullPointerException("Test Service NPE"));
        Pageable testPageable = PageRequest.of(0, 5, Sort.unsorted());
        ResponseEntity<Page<TaskDTO>> results = taskController.getPaginatedTasks(testPageable);
        Assertions.assertNotNull(results);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, results.getStatusCode());
    }

    private List<TaskDTO> getTestTasks() {
        List<TaskDTO> taskList = new ArrayList<>();
        String currentDateString = LocalDateTime.now().toString().split("T")[0];
        String previousDateString = LocalDateTime.now().minusDays(1l).toString().split("T")[0];
        String futureDateString = LocalDateTime.now().plusDays(1l).toString().split("T")[0];
        taskList.add(
                new TaskDTO("1", "testOwner", "Test Task 1", "A task for testing", null, null,
                        currentDateString + "T09:45:00", 5, Duration.M.toString(), false, false,
                        Collections.emptyList()));
        taskList.add(
                new TaskDTO("2", "testOwner", "Test Task 2", "A task for testing", null, null,
                        currentDateString + "T09:30:00", 5, Duration.M.toString(), false, false,
                        Collections.emptyList()));
        taskList.add(
                new TaskDTO("3", "testOwner", "Test Task 3", "A task with a null due date", null, null,
                        null, 5, Duration.M.toString(), false, false, Collections.emptyList()));
        taskList.add(
                new TaskDTO("4", "testOwner", "Test Task 4", "A task that was due yesterday", null, null,
                        previousDateString + "T09:15:00", 5, Duration.M.toString(), false, false,
                        Collections.emptyList()));
        taskList.add(
                new TaskDTO("5", "testOwner", "Test Task 5", "A task that is due tomorrow", null, null,
                        futureDateString + "T14:15:00", 5, Duration.M.toString(), false, false,
                        Collections.emptyList()));

        return taskList;
    }

}
