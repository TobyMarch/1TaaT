package com.taat.taskservices.controllers;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

  @Autowired
  private TaskRepository taskRepository;

  @GetMapping("/tasks")
  public ResponseEntity<Flux<Task>> getTasks() {
    Flux<Task> tasks = taskRepository.findAll();
    return new ResponseEntity<>(tasks, HttpStatus.OK);
  }

  @PostMapping("/tasks")
  public ResponseEntity<?> addNewTasks(@RequestBody List<Task> tasks) {
    taskRepository.insert(tasks).subscribe();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
