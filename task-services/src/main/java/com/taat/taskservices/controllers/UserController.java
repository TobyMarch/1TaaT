package com.taat.taskservices.controllers;

import com.taat.taskservices.model.User;
import com.taat.taskservices.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping("/user")
  public ResponseEntity<?> getUser(@AuthenticationPrincipal OAuth2User user) {
    if (user == null) {
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      return ResponseEntity.ok().body(user.getAttributes());
    }
  }

  @GetMapping("/checkUserRefreshToken")
  public ResponseEntity<Boolean> checkRefreshToken(@AuthenticationPrincipal OAuth2User principal) {
    String userId = principal.getAttributes().get("sub").toString();
    System.out.println(userId);
    Map<String, Object> userDetails = principal.getAttributes();
    User user = userService.getOrAddUser(userDetails);
    boolean result = user.getCalendarRefreshToken() != null;
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
