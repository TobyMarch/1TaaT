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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.repository.TaskRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    TaskRepository taskRepo;

    @InjectMocks
    TaskService taskService;

    @Test
    public void testCreateUpdateTasks() {
        Task testTask = new Task("1", "testOwner", "Test Task", "A task for testing", null, null, null, 5,
                false, false);
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
    public void testGetPaginatedTasks() {
        Flux<Task> taskFlux = Flux.fromIterable(getTestTasks());
        Mockito.when(taskRepo.findAllBy(Mockito.any(Pageable.class))).thenReturn(taskFlux);

        Pageable testPageable = PageRequest.of(0, 5, Sort.unsorted());
        Flux<Task> results = taskService.getPaginatedTasks(testPageable);
        Assertions.assertNotNull(results);
        Mockito.verify(taskRepo, Mockito.times(1)).findAllBy(Mockito.any(Pageable.class));
    }

    @Test
    public void testGetTaskCount() {
        Mockito.when(taskRepo.count()).thenReturn(Mono.just(5l));
        taskService.getTaskCount().subscribe(countValue -> Assertions.assertEquals(countValue, 5l));
    }

    @Test
    public void testPrioritySortTasks_DueDate() {
        List<Task> unsortedList = getTestTasks();

        List<Task> sortedList = taskService.prioritySortTasks(unsortedList);
        Assertions.assertNotNull(sortedList);
        Assertions.assertEquals(unsortedList.size(), sortedList.size());
    }
    @Test
    public void testDeleteById() {
        String taskId = "1";
        Mockito.when(taskRepo.deleteById(taskId)).thenReturn(Mono.empty());

        Mono<Void> result = taskService.deleteById(taskId);

        StepVerifier.create(result).verifyComplete();
        Mockito.verify(taskRepo, Mockito.times(1)).deleteById(taskId);
    }

    @Test
    public void testArchiveTask() {
        String taskId = "1";
        Task testTask = new Task(taskId, "testOwner", "Test Task", "A task for testing", null, null, null, 5, false, false);
        
        Mockito.when(taskRepo.findById(taskId)).thenReturn(Mono.just(testTask));
        Mockito.when(taskRepo.save(testTask)).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Task> result = taskService.archiveTask(taskId);

        StepVerifier.create(result).assertNext(archivedTask -> {
                Assertions.assertTrue(archivedTask.isArchived());
                Assertions.assertEquals(taskId, archivedTask.getId());
            }).verifyComplete();

        Mockito.verify(taskRepo, Mockito.times(1)).findById(taskId);
        Mockito.verify(taskRepo, Mockito.times(1)).save(testTask);
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
                new Task("4", "testOwner", "Test Task 5", "A task that is due tomorrow", null, null,
                        futureDateString + "T14:15:00", 5, false, false));

        return taskList;
    }

}
