package com.taat.taskservices.repository.imperative;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.taat.taskservices.model.Task;

@Repository
public interface ImperativeTaskRepository extends MongoRepository<Task, String> {
}
