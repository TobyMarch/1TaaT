package com.taat.taskservices.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @GetMapping("/user")
  public ResponseEntity<?> getUser(@AuthenticationPrincipal OAuth2User principal) {
    if (principal == null) {
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      return ResponseEntity.ok().body(principal.getAttributes());
    }
  }
}
