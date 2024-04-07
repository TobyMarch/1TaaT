package com.taat.taskservices.services;

import com.taat.taskservices.model.User;
import com.taat.taskservices.repository.imperative.ImperativeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

  @Autowired
  ImperativeUserRepository userRepository;

  public User getOrAddUser(Map<String, Object> userDetails) {
    String userId = userDetails.get("sub").toString();
    Optional<User> user = userRepository.findById(userId);
    return user.orElse(userRepository.save(new User(userId)));
  }
}
