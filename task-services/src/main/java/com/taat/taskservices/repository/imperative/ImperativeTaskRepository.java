package com.taat.taskservices.repository.imperative;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.taat.taskservices.model.Task;

@Repository
public interface ImperativeTaskRepository extends MongoRepository<Task, String> {
    List<Task> findAllBy(Pageable pageable);
}
