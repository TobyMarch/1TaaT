package com.taat.taskservices.repository.imperative;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.model.UserTask;

@Repository
public interface ImperativeUserTaskRepository extends MongoRepository<UserTask, String> {

    @Aggregation(pipeline = { "{$match: {userId: '?0', archived: {$ne : true}}}", "{$sort: { sortValue: -1 }}",
            "{$addFields: {taskId: {$toObjectId: \"$taskId\"} }}",
            "{$lookup: {from: \"tasksTest\", localField: \"taskId\", foreignField:\"_id\", as: \"result\"}}",
            "{$addFields: {result: {$first: \"$result\"}}}", "{$match: {result: {$ne: null}}}", "{$limit: 1}",
            "{$replaceRoot: {newRoot:\"$result\"}}" })
    Task findTopTaskByUserTaskSort(String userId);

    @Aggregation(pipeline = { "{$match: {userId: '?0', archived: true}}",
            "{$addFields: {taskId: {$toObjectId: \"$taskId\"} }}",
            "{$lookup: {from: \"tasksTest\", localField: \"taskId\", foreignField:\"_id\", as: \"result\"}}",
            "{$addFields: {result: {$first: \"$result\"}}}", "{$match: {result: {$ne: null}}}",
            "{$replaceRoot: {newRoot:\"$result\"}}", "{$sort: {dueDate: -1}}", "{$skip: ?1}", "{$limit: ?2}" })
    List<Task> findArchivedTasksByUserIdPaginated(String userId, int skip, int limit);

    @Query(value = "{userId: '?0', archived: true}", count = true)
    Long getArchivedTaskCountByUserId(String userId);

    @Query("{userId: '?0', taskId: '?1'}")
    UserTask findByUserIdTaskId(String userId, String taskId);

    @Query("{userId: '?0'}")
    List<UserTask> findByUserId(String userId);

    @Query("{taskId: '?0'}")
    List<UserTask> findByTaskId(String taskId);

    void deleteByTaskId(String taskId);
}
