package com.taat.taskservices.services;

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
import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    TaskRepository taskRepo;

    @InjectMocks
    TaskService taskService;

    @Test
    public void testCreateUpdateTasks() {
        Task testTask = new Task("1", "testOwner", "Test Task", "A task for testing", null, null, null, 5, false);
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

}
