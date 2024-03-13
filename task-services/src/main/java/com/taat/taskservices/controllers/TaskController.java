package com.taat.taskservices.controllers;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

  @Autowired
  TaskService taskService;

  @CrossOrigin(origins = { "http://localhost:3000", "https://onetaat-web.onrender.com" })
  @GetMapping("/tasks")
  public ResponseEntity<Flux<Task>> getTasks() {
    Flux<Task> tasks = taskService.getPrioritizedTasks();
    return new ResponseEntity<>(tasks, HttpStatus.OK);
  }

  @CrossOrigin(origins = { "http://localhost:3000", "https://onetaat-web.onrender.com" })
  @PostMapping("/tasks")
  public ResponseEntity<?> addNewTasks(@RequestBody List<Task> tasks) {
    taskService.createUpdateTasks(tasks).subscribe();
    return new ResponseEntity<>(HttpStatus.OK);
  }


  // delete task
  @CrossOrigin(origins = { "http://localhost:3000", "https://onetaat-web.onrender.com" })
  @DeleteMapping("/tasks/{id}")
  public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
    return taskService.deleteById(id)
      .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
      .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  // archive task
  @CrossOrigin(origins = { "http://localhost:3000", "https://onetaat-web.onrender.com" })
  @PostMapping("/tasks/{id}/archive")
  public Mono<ResponseEntity<Task>> archiveTask(@PathVariable String id) {
    return taskRepository.findById(id)
      .flatMap(task -> {
        task.setArchived(true); 
        return taskRepository.save(task);
      })
      .map(updatedTask -> new ResponseEntity<>(updatedTask, HttpStatus.OK))
      .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
