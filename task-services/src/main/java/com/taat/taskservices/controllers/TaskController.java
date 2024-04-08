package com.taat.taskservices.controllers;

import java.util.List;
import java.util.Map;

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
import com.taat.taskservices.services.ImperativeTaskService;
// import com.taat.taskservices.services.TaskService;
import com.taat.taskservices.services.UserService;

// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    // @Autowired
    // TaskService taskService;

    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    ImperativeTaskService taskService;

    @Autowired
    UserService userService;

    // @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<Flux<Task>> getTasks() {
    // Flux<Task> tasks = taskService.getPrioritizedTasks();
    // return new ResponseEntity<>(tasks, HttpStatus.OK);
    // }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Task>> getTasks(@AuthenticationPrincipal OAuth2User principal) {
        String userId = principal.getAttributes().get("sub").toString();
        List<Task> tasks = taskService.getPrioritizedTasks(userId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // @GetMapping(path = "/top", produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<Mono<Task>> getTopTask() {
    // Mono<Task> tasks = taskService.getTopTask("");
    // return new ResponseEntity<>(tasks, HttpStatus.OK);
    // }

    @GetMapping(path = "/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> getTopTask() {
        try {
            TaskDTO tasks = taskService.getTopTask("");
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            logger.warn("Exception in Top Task retrieval: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<Mono<Page<Task>>> getPaginatedTasks(Pageable pageable)
    // {
    // Mono<Page<Task>> tasks = taskService.getPaginatedTasks(pageable)
    // .collectList().zipWith(taskService.getTaskCount())
    // .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    // return new ResponseEntity<>(tasks, HttpStatus.OK);
    // }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<TaskDTO>> getPaginatedTasks(Pageable pageable) {
        try {
            Page<TaskDTO> resultPage = taskService.getPaginatedTasks("", pageable);
            return new ResponseEntity<>(resultPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.warn("Exception in Task List retrieval: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(path = "/archived", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Task>> getArchivedTasks(Pageable pageable) {
        Page<Task> resultPage = taskService.getArchivedTasks("", pageable);
        return new ResponseEntity<>(resultPage, HttpStatus.OK);
    }

    // @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces =
    // MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<Mono<List<Task>>> addNewTasks(@RequestBody List<Task>
    // tasks) {
    // Mono<List<Task>> taskFlux = taskService.createUpdateTasks(tasks);
    // return new ResponseEntity<>(taskFlux, HttpStatus.CREATED);
    // }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Task>> addNewTasks(@RequestBody List<Task> tasks,
                                                  @AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> userDetails = principal.getAttributes();
        User user = userService.getOrAddUser(userDetails);
        for (Task task : tasks) {
            task.setOwner(user.getUserId());
        }

        List<Task> taskFlux = taskService.createUpdateTasks(tasks, user.getUserId());
        return new ResponseEntity<>(taskFlux, HttpStatus.CREATED);
    }

    // @DeleteMapping("/{id}")
    // public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
    // return taskService.deleteById(id)
    // .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
    // .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        if (taskService.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // @PutMapping(path = "/{id}/archive", produces =
    // MediaType.APPLICATION_JSON_VALUE)
    // public Mono<ResponseEntity<Task>> archiveTask(@PathVariable String id) {
    // return taskService.archiveTask(id)
    // .map(updatedTask -> new ResponseEntity<>(updatedTask, HttpStatus.OK))
    // .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    // }

    @PutMapping(path = "/{id}/archive", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> archiveTask(@PathVariable String id) {
        Task result = taskService.archiveTask(id);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
