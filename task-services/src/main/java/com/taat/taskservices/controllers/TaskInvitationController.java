package com.taat.taskservices.controllers;

import com.taat.taskservices.model.TaskInvitation;
import com.taat.taskservices.repository.TaskInvitationRepository;
import com.taat.taskservices.repository.UserRepository;
import com.taat.taskservices.repository.UserTaskRepository;
import com.taat.taskservices.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  @PostMapping("/invite")
  public void inviteUserToTask(@AuthenticationPrincipal OAuth2User principal,
                               @RequestBody TaskInvitation invitation) {
    if (
      userRepository.existsByUserEmail(invitation.getUserEmail()) &&
      userTaskRepo.existsByTaskIdAndUserId(invitation.getTaskId(), principal.getName()) &&
      !invitationRepository.existsByUserEmailAndTaskId(invitation.getUserEmail(), invitation.getTaskId())
    ) {
      invitationRepository.save(invitation);
    }
  }

  @GetMapping
  public void getInvitations(@AuthenticationPrincipal OAuth2User principal) {
    List<TaskInvitation> invitations = invitationRepository.findAllByUserEmail(principal.getAttribute("email"));
  }

  @PostMapping("/accept")
  public void acceptInvitation(@AuthenticationPrincipal OAuth2User principal,
                               @RequestBody TaskInvitation invitation) {
    if (principal.getAttribute("email") == invitation.getUserEmail()) {
        taskService.acceptTaskInvitation(invitation.getTaskId(), principal.getName());
    }
  }
}
