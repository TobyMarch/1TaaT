package com.taat.taskservices.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.model.UserTask;

@Repository
public interface UserTaskRepository extends MongoRepository<UserTask, String> {

    @Aggregation(pipeline = { "{$match: {userId: '?0', archived: {$ne : true}}}", "{$sort: { sortValue: -1 }}",
            "{$limit: 1}" })
    UserTask findTopUserTask(String userId);

    @Aggregation(pipeline = {
            "{$match: {userId: '?0', archived: {$ne : true}, $or: [{skipUntil: {$eq: null}}, {skipUntil: {$lte: ?1}}]}}",
            "{$sort: { sortValue: -1 }}", "{$addFields: {taskId: {$toObjectId: \"$taskId\"} }}",
            "{$lookup: {from: \"tasksTest\", localField: \"taskId\", foreignField:\"_id\", as: \"result\"}}",
            "{$match: {result: {$ne: []}}}", "{$addFields: {result: {$first: \"$result\"}}}",
            "{$match: {result: {$ne: null}}}", "{$limit: 1}", "{$replaceRoot: {newRoot:\"$result\"}}" })
    Task findTopTaskByUserTaskSort(String userId, Instant skipUntil);

    @Aggregation(pipeline = { "{$match: {userId: '?0', archived: {$ne : true}}}",
            "{$addFields: {taskId: {$toObjectId: \"$taskId\"} }}",
            "{$lookup: {from: \"tasksTest\", localField: \"taskId\", foreignField: \"_id\", as: \"result\"}}",
            "{$match: {result: {$ne: []}}}", "{$addFields: {result: {$first: \"$result\"}}}",
            "{$match: {result: {$ne: null}}}", "{$sort: {\"?1\":?2}}", "{$unset: \"result\"}" })
    List<UserTask> findUserTasksByUserIdSortParams(String userId, String sortBy, Integer sortOrder);

    @Aggregation(pipeline = { "{$match: {userId: '?0', archived: {$ne : true}}}",
            "{$addFields: {taskId: {$toObjectId: \"$taskId\"} }}",
            "{$lookup: {from: \"tasksTest\", localField: \"taskId\", foreignField: \"_id\", as: \"result\"}}",
            "{$match: {result: {$ne: []}}}", "{$addFields: {result: {$first: \"$result\"}}}",
            "{$match: {result: {$ne: null}}}", "{$replaceRoot: {newRoot: \"$result\"}}",
            "{$unwind: {path: \"$subTasks\",preserveNullAndEmptyArrays: false}}",
            "{$addFields: {subTasks: {$toObjectId: \"$subTasks\"}}}",
            "{$lookup: {from: \"tasksTest\",localField: \"subTasks\",foreignField: \"_id\",as: \"subTasks\"}}",
            "{$match: {subTasks: {$ne: []}}}", "{$addFields: {subTasks: {$first: \"$subTasks\"}}}",
            "{$match: {subTasks: {$ne: null}}}", "{$replaceRoot: {newRoot: \"$subTasks\"}}" })
    List<Task> findSubTasksByUserId(String userId);

    @Aggregation(pipeline = { "{$match: {userId: '?0', archived: true}}",
            "{$addFields: {taskId: {$toObjectId: \"$taskId\"} }}",
            "{$lookup: {from: \"tasksTest\", localField: \"taskId\", foreignField:\"_id\", as: \"result\"}}",
            "{$match: {subTasks: {$ne: []}}}", "{$addFields: {result: {$first: \"$result\"}}}",
            "{$match: {result: {$ne: null}}}", "{$replaceRoot: {newRoot:\"$result\"}}", "{$sort: {dueDate: -1}}",
            "{$skip: ?1}", "{$limit: ?2}" })
    List<Task> findArchivedTasksByUserIdPaginated(String userId, int skip, int limit);

    @Query(value = "{userId: '?0', archived: true}", count = true)
    Long getArchivedTaskCountByUserId(String userId);

    @Query("{userId: '?0', taskId: '?1'}")
    UserTask findByUserIdTaskId(String userId, String taskId);

    @Query("{userId: '?0'}")
    List<UserTask> findByUserId(String userId);

    @Query("{taskId: {$in : ?0}}")
    List<UserTask> findByTaskIds(List<String> taskId);

    void deleteByTaskId(String taskId);

    Boolean existsByTaskIdAndUserId(String taskId, String userId);
}
