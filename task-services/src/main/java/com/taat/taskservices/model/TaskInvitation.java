package com.taat.taskservices.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("taskInivation")
public class TaskInvitation {

  @Id
  private String id;
  private String userEmail;
  private String taskTitle;
  private String taskId;
}
