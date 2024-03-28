package com.taat.taskservices.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
// import org.springframework.stereotype.Repository;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.model.UserTask;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// @Repository
public interface UserTaskRepository extends ReactiveMongoRepository<UserTask, String> {

    @Aggregation(pipeline = { "{$match: {userId: '?0', archived: {$ne : true}}}", "{$sort: { sortValue: -1 }}",
            "{$addFields: {taskId: {$toObjectId: \"$taskId\"} }}",
            "{$lookup: {from: \"tasksTest\", localField: \"taskId\", foreignField:\"_id\", as: \"result\"}}",
            "{$addFields: {result: {$first: \"$result\"}}}", "{$match: {result: {$ne: null}}}", "{$limit: 1}",
            "{$replaceRoot: {newRoot:\"$result\"}}" })
    Mono<Task> findTopTaskByUserTaskSort(String userId);

    @Query("{userId: '?0', taskId: '?1'}")
    Mono<UserTask> findUserTaskByUserIdTaskId(String userId, String taskId);

    @Query("{userId: '?0'}")
    Flux<UserTask> findByUserId(String userId);

    @Query("{taskId: '?0'}")
    Flux<UserTask> findByTaskId(String taskId);

    Flux<UserTask> deleteByTaskId(String taskId);
}
