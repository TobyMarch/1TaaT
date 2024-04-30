package com.taat.taskservices.controllers;

import com.taat.taskservices.model.TaskInvitation;
import com.taat.taskservices.repository.TaskInvitationRepository;
import com.taat.taskservices.repository.UserRepository;
import com.taat.taskservices.repository.UserTaskRepository;
import com.taat.taskservices.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/invitations")
public class TaskInvitationController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  UserTaskRepository userTaskRepo;

  @Autowired
  UserRepository userRepository;

  @Autowired
  TaskInvitationRepository invitationRepository;

  @Autowired
  TaskService taskService;

  //TODO: confirm deletion for all users if one deletes
  //TODO: ability to leave task?
  //TODO: validity helper functions
  //TODO: services

  @PostMapping("/invite")
  public ResponseEntity<String> inviteUserToTask(@AuthenticationPrincipal OAuth2User principal,
                                                 @RequestBody TaskInvitation invitation) {
    if (
      userRepository.existsByUserEmail(invitation.getUserEmail()) &&
      userTaskRepo.existsByTaskIdAndUserId(invitation.getTaskId(), principal.getName()) &&
      invitation.getUserEmail() != principal.getAttribute("email") &&
      !invitationRepository.existsByUserEmailAndTaskId(invitation.getUserEmail(), invitation.getTaskId())
    ) {
      invitationRepository.save(invitation);
      return new ResponseEntity<>("success", HttpStatus.OK);
    }
    return new ResponseEntity<>("failed", HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<TaskInvitation>> getInvitations(@AuthenticationPrincipal OAuth2User principal) {
    List<TaskInvitation> invitations = invitationRepository.findAllByUserEmail(principal.getAttribute("email"));
    return new ResponseEntity<>(invitations, HttpStatus.OK);
  }

  @PostMapping("/accept")
  public ResponseEntity<String> acceptInvitation(@AuthenticationPrincipal OAuth2User principal,
                                                 @RequestBody TaskInvitation invitation) {
    if (Objects.equals(principal.getAttribute("email").toString(), invitation.getUserEmail())) {
      taskService.acceptTaskInvitation(invitation.getTaskId(), principal.getName());
      invitationRepository.delete(invitation);
      return new ResponseEntity<>("success", HttpStatus.OK);
    }
    return new ResponseEntity<>("failed", HttpStatus.OK);
  }

  @DeleteMapping("/reject")
  public ResponseEntity<String> rejectInvitation(@AuthenticationPrincipal OAuth2User principal,
                                                 @RequestBody TaskInvitation invitation) {
    if (Objects.equals(principal.getAttribute("email").toString(), invitation.getUserEmail())) {
      invitationRepository.delete(invitation);
      return new ResponseEntity<>("success", HttpStatus.OK);
    }
    return new ResponseEntity<>("failed", HttpStatus.OK);
  }
}
