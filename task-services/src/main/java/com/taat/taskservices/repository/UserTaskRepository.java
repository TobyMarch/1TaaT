package com.taat.taskservices.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.taat.taskservices.model.UserTask;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserTaskRepository extends ReactiveMongoRepository<UserTask, String> {

    @Query("{userId: '?0', taskId: '?1'}")
    Mono<UserTask> findUserTaskByUserIdTaskId(String userId, String taskId);

    @Query("{userId: '?0'}")
    Flux<UserTask> findByUserId(String userId);
}
