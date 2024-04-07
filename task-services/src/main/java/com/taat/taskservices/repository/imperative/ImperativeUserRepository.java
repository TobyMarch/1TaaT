package com.taat.taskservices.repository.imperative;

import com.taat.taskservices.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImperativeUserRepository extends MongoRepository<User, String> {
}
