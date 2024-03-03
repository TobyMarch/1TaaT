package com.taat.taskservices.repository;

import com.taat.taskservices.model.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends ReactiveMongoRepository<Task, String> {

}
