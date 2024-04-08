package com.taat.taskservices.model;

import com.google.api.client.util.DateTime;
import com.taat.taskservices.utils.Duration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("tasksTest")
public class Task {

  @Id
  private String id;

  private String owner;

  private String title;

  private String description;

  private String createdDate;

  private String startDate;

  private String dueDate;

  private int priority;

  private String duration;

  private boolean isDelayable;

  private boolean archived;

  private List<String> subTasks = new ArrayList<>();
}
