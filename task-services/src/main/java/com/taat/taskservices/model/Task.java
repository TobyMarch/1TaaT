package com.taat.taskservices.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("tasksTest")
public class Task {

  @Id
  private String id;

  private String externalId;

  private String owner;

  private String title;

  private String description;

  private Instant createdDate;

  private Instant startDate;

  private Instant dueDate;

  private int priority;

  private String duration;

  private List<String> recurrence;

  private boolean delayable;

  private boolean archived;

  private List<String> subTasks = new ArrayList<>();
}
