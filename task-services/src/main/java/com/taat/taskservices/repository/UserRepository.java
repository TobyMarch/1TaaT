package com.taat.taskservices.repository;

import com.taat.taskservices.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
  Boolean existsByUserEmail(String userEmail);
}
