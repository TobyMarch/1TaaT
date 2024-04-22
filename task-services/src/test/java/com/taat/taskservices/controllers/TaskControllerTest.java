package com.taat.taskservices.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.taat.taskservices.dto.TaskDTO;
import com.taat.taskservices.model.User;
import com.taat.taskservices.services.ImperativeTaskService;
import com.taat.taskservices.services.UserService;
import com.taat.taskservices.utils.Duration;


@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    ImperativeTaskService taskService;

    @Mock
    UserService userService;

    @InjectMocks
    TaskController taskController;

    @Test
    public void testGetTopTask_Imperative() {
        OAuth2User principal = getTestUserPrincipal();
        TaskDTO taskDto = getTestTasks().get(0);
        Mockito.when(taskService.getTopTask(Mockito.anyString())).thenReturn(taskDto);
        ResponseEntity<TaskDTO> results = taskController.getTopTask(principal);
        Assertions.assertNotNull(results);
        Mockito.verify(taskService, Mockito.times(1)).getTopTask(Mockito.eq(principal.getAttribute("sub")));
    }

    @Test
    public void testGetTopTask_Exception() {
        OAuth2User principal = getTestUserPrincipal();
        Mockito.when(taskService.getTopTask(Mockito.anyString()))
                .thenThrow(new NullPointerException("Test Service NPE"));
        ResponseEntity<TaskDTO> results = taskController.getTopTask(principal);
        Assertions.assertNotNull(results);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, results.getStatusCode());
    }

    @Test
    public void testGetPaginatedTasks_Imperative() {
        OAuth2User principal = getTestUserPrincipal();
        List<TaskDTO> taskList = getTestTasks();
        Mockito.when(taskService.getPaginatedTasks(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<TaskDTO>(taskList));

        Pageable testPageable = PageRequest.of(0, 5, Sort.unsorted());
        ResponseEntity<Page<TaskDTO>> results = taskController.getPaginatedTasks(testPageable, principal);
        Assertions.assertNotNull(results);
        Mockito.verify(taskService, Mockito.times(1)).getPaginatedTasks(Mockito.eq(principal.getAttribute("sub")),
                Mockito.any(Pageable.class));
    }

    @Test
    public void testGetPaginatedTasks_Exception() {
        OAuth2User principal = getTestUserPrincipal();
        Mockito.when(taskService.getPaginatedTasks(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenThrow(new NullPointerException("Test Service NPE"));
        Pageable testPageable = PageRequest.of(0, 5, Sort.unsorted());
        ResponseEntity<Page<TaskDTO>> results = taskController.getPaginatedTasks(testPageable, principal);
        Assertions.assertNotNull(results);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, results.getStatusCode());
    }

    @Test
    public void testGetArchivedTasks_Imperative() {
        OAuth2User principal = getTestUserPrincipal();
        List<TaskDTO> taskList = getTestTasks();
        Page<TaskDTO> pageData = new PageImpl<>(taskList);

        Pageable testPageable = PageRequest.of(0, taskList.size(), Sort.unsorted());
        Mockito.when(taskService.getArchivedTasks(Mockito.anyString(), Mockito.eq(testPageable))).thenReturn(pageData);

        ResponseEntity<Page<TaskDTO>> results = taskController.getArchivedTasks(testPageable, principal);
        Assertions.assertNotNull(results);
        Mockito.verify(taskService, Mockito.times(1)).getArchivedTasks(Mockito.eq(principal.getAttribute("sub")),
                Mockito.any(Pageable.class));
    }

    @Test
    public void testSaveTasks() {
        OAuth2User principal = getTestUserPrincipal();
        List<TaskDTO> taskList = getTestTasks();

        Mockito.when(userService.getOrAddUser(Mockito.anyMap())).thenReturn(getTestUser());
        Mockito.when(taskService.createUpdateTasks(Mockito.anyList(), Mockito.eq(principal.getAttribute("sub"))))
                .thenReturn(taskList);
        ResponseEntity<List<TaskDTO>> results = taskController.saveTasks(taskList, principal);
        Assertions.assertNotNull(results);
        Assertions.assertEquals(HttpStatus.CREATED, results.getStatusCode());
    }

    @Test
    public void testSaveTasks_Exception() {
        OAuth2User principal = getTestUserPrincipal();
        List<TaskDTO> taskList = getTestTasks();

        Mockito.when(userService.getOrAddUser(Mockito.anyMap())).thenReturn(getTestUser());
        Mockito.when(taskService.createUpdateTasks(Mockito.anyList(), Mockito.eq(principal.getAttribute("sub"))))
                .thenReturn(null);
        ResponseEntity<List<TaskDTO>> results = taskController.saveTasks(taskList, principal);
        Assertions.assertNotNull(results);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, results.getStatusCode());
    }

    private OAuth2User getTestUserPrincipal() {
        String userName = "testuser";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", userName);
        OAuth2User principal = new DefaultOAuth2User(Collections.emptyList(), attributes, "sub");
        return principal;
    }

    private User getTestUser() {
        return new User("testuser");
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
