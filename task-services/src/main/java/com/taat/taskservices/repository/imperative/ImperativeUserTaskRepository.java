package com.taat.taskservices.repository.imperative;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.taat.taskservices.model.UserTask;

@Repository
public interface ImperativeUserTaskRepository extends MongoRepository<UserTask, String> {

    @Query("{userId: '?0', taskId: '?1'}")
    UserTask findByUserIdTaskId(String userId, String taskId);

    @Query("{userId: '?0'}")
    List<UserTask> findByUserId(String userId);
}
