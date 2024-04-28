package com.taat.taskservices.controllers;

import com.taat.taskservices.services.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
  @Autowired
  CalendarService calendarService;

  @GetMapping("/getAccessToken")
  public ResponseEntity<String> getAccessToken(@AuthenticationPrincipal OAuth2User principal) {
    if (principal == null) {
      return new ResponseEntity<>("", HttpStatus.OK);
    }
    String token = calendarService.getAccessToken(principal.getName());
    return new ResponseEntity<>(token, HttpStatus.OK);
  }

  @GetMapping("/refreshAccessToken")
  public ResponseEntity<String> refreshAccessToken(@AuthenticationPrincipal OAuth2User principal) {
    if (principal == null) {
      return new ResponseEntity<>("", HttpStatus.OK);
    }
    String token = calendarService.refreshAccessToken(principal.getName());
    HttpStatus status = token == null ? HttpStatus.UNAUTHORIZED : HttpStatus.OK;
    return new ResponseEntity<>(token, status);
  }
}
