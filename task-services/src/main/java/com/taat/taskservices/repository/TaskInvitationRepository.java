package com.taat.taskservices.repository;

import com.taat.taskservices.model.TaskInvitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskInvitationRepository extends MongoRepository<TaskInvitation, String> {
  Boolean existsByUserEmailAndTaskId(String userEmail, String taskId);
  List<TaskInvitation> findAllByUserEmail(String userEmail);
}
