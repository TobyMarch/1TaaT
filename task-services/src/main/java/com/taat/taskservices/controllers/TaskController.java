package com.taat.taskservices.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.services.TaskService;

import reactor.core.publisher.Flux;

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
}
