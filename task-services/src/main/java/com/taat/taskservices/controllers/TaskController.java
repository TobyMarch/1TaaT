package com.taat.taskservices.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.services.ImperativeTaskService;
// import com.taat.taskservices.services.TaskService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = { "http://localhost:3000", "https://onetaat-web.onrender.com" })
public class TaskController {

    // @Autowired
    // TaskService taskService;

    @Autowired
    ImperativeTaskService taskService;

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Task>> getTasks() {
        List<Task> tasks = taskService.getPrioritizedTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // @GetMapping(path = "/top", produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<Mono<Task>> getTopTask() {
    // Mono<Task> tasks = taskService.getTopTask("");
    // return new ResponseEntity<>(tasks, HttpStatus.OK);
    // }

    // @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<Mono<Page<Task>>> getPaginatedTasks(Pageable pageable)
    // {
    // Mono<Page<Task>> tasks = taskService.getPaginatedTasks(pageable)
    // .collectList().zipWith(taskService.getTaskCount())
    // .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    // return new ResponseEntity<>(tasks, HttpStatus.OK);
    // }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Task>> addNewTasks(@RequestBody List<Task> tasks) {
      List<Task> taskFlux = taskService.createUpdateTasks(tasks);
      return new ResponseEntity<>(taskFlux, HttpStatus.CREATED);
  }

  // @DeleteMapping("/{id}")
  // public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
  // return taskService.deleteById(id)
  // .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
  // .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  // }

  // @PutMapping(path = "/{id}/archive", produces =
  // MediaType.APPLICATION_JSON_VALUE)
  // public Mono<ResponseEntity<Task>> archiveTask(@PathVariable String id) {
  // return taskService.archiveTask(id)
  // .map(updatedTask -> new ResponseEntity<>(updatedTask, HttpStatus.OK))
  // .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  // }
}
