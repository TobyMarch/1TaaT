package com.taat.taskservices.controllers;

import com.taat.taskservices.model.User;
import com.taat.taskservices.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private UserRepository userRepository;

  @CrossOrigin(origins = { "http://localhost:3000", "https://onetaat-web.onrender.com" })
  @PostMapping("/addUser")
  public ResponseEntity<Mono<Boolean>> addNewUser(@RequestBody User newUser) {

    Mono<Boolean> userAdded = userRepository.findById(newUser.getUserId())
            .map(Optional::of).defaultIfEmpty(Optional.empty())
            .flatMap(existingUser -> {
              if (existingUser.isEmpty()) {
                return userRepository.insert(newUser).map(saved -> true);
              }
              return Mono.empty();
            })
            .defaultIfEmpty(false);

    return new ResponseEntity<>(userAdded, HttpStatus.OK);
  }

  @CrossOrigin(origins = { "http://localhost:3000", "https://onetaat-web.onrender.com" })
  @GetMapping("/checkUserRefreshToken")
  public ResponseEntity<Mono<Boolean>> checkRefreshToken(@RequestParam String userId) {
    Mono<Boolean> hasRefreshToken = userRepository.findById(userId)
            .map(user -> {
              if(user.getCalendarRefreshToken() == null) return false;
              return true;
            });
    return new ResponseEntity<>(hasRefreshToken, HttpStatus.OK);
  }
}
