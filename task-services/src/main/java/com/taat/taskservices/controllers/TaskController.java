package com.taat.taskservices.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taat.taskservices.dto.TaskDTO;
import com.taat.taskservices.model.Task;
import com.taat.taskservices.model.User;
import com.taat.taskservices.model.UserTask;
import com.taat.taskservices.services.ImperativeTaskService;
import com.taat.taskservices.services.UserService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    ImperativeTaskService taskService;

    @Autowired
    UserService userService;

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Task>> getTasks(@AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttributes().get("sub").toString();
        List<Task> tasks = taskService.getPrioritizedTasks(userId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping(path = "/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> getTopTask(@AuthenticationPrincipal OAuth2User principal) {
        try {
            String userId = principal.getAttributes().get("sub").toString();
            TaskDTO tasks = taskService.getTopTask(userId);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            logger.warn("Exception in Top Task retrieval: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TaskDTO>> getPaginatedTasks(Pageable pageable,
                                                           @AuthenticationPrincipal OAuth2User principal) {
        try {
            String userId = principal.getAttributes().get("sub").toString();
            Page<TaskDTO> resultPage = taskService.getPaginatedTasks(userId, pageable);
            return new ResponseEntity<>(resultPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.warn("Exception in Task List retrieval: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(path = "/archived", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TaskDTO>> getArchivedTasks(Pageable pageable,
                                                          @AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttributes().get("sub").toString();
        Page<TaskDTO> resultPage = taskService.getArchivedTasks(userId, pageable);
        return new ResponseEntity<>(resultPage, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskDTO>> saveTasks(@RequestBody List<TaskDTO> tasksDTOs,
                                                  @AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> userDetails = principal.getAttributes();
        User user = userService.getOrAddUser(userDetails);

        List<TaskDTO> taskList = taskService.createUpdateTasks(tasksDTOs, user.getUserId());
        if (taskList != null) {
            return new ResponseEntity<>(taskList, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id,
                                           @AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttributes().get("sub").toString();
        if (taskService.deleteById(id, userId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/{id}/archive", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> archiveTask(@PathVariable String id,
                                            @AuthenticationPrincipal OAuth2User principal) {
        try {
            String userId = principal.getAttributes().get("sub").toString();
            TaskDTO result = taskService.archiveTask(id, userId);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{id}/skip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTask> skipTask(@PathVariable String id, @AuthenticationPrincipal OAuth2User principal) {
        try {
            String userId = principal.getAttributes().get("sub").toString();
            Optional<UserTask> skipResult = taskService.skipTask(id, userId);
            if (skipResult.isPresent()) {
                return new ResponseEntity<>(skipResult.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.warn("Exception while skipping Task: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
