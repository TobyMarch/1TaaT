package com.taat.taskservices.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.taat.taskservices.dto.TaskDTO;
import com.taat.taskservices.model.Task;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findAllBy(Pageable pageable);

    List<Task> findAllByOwner(String owner);

    Boolean existsByOwnerAndId(String owner, String id);

    Boolean existsByExternalId(String externalId);

    @Aggregation(pipeline = { "{$match: {_id: ObjectId('?0')}}",
            "{$unwind: {path: \"$subTasks\", preserveNullAndEmptyArrays: true}}",
            "{$addFields: {subTasks: {$toObjectId: \"$subTasks\"}}}",
            "{$lookup: {from: \"tasksTest\", localField: \"subTasks\", foreignField:\"_id\", as: \"subTasks\"}}",
            "{$group: {_id: \"$_id\", merged: {$first: \"$$ROOT\"}, subTasks: {$push:{$first: \"$subTasks\"}}}}",
            "{$addFields: {\"merged.subTasks\": \"$subTasks\"}}",
            "{$replaceRoot: {newRoot: \"$merged\"}}" })
    TaskDTO buildHierarchicalRecordById(String id);
}
