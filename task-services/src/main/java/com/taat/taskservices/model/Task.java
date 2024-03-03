package com.taat.taskservices.model;

import com.google.api.client.util.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document("tasksTest")
public class Task {

  private String summary;
//  private DateTime start;
  String start;
  private int priority;
  private String calendarId;

  public Task(String summary, String start, int priority, String calendarId) {
    this.summary = summary;
    this.start = start;
    this.priority = priority;
    this.calendarId = calendarId;
  }

  public String getSummary() {
    return summary;
  }

  public String getStart() {
    return start;
  }

  public int getPriority() {
    return priority;
  }

  public String getCalendarId() {
    return calendarId;
  }

  public void display() {
    System.out.println(
            this.summary + "\nstart: " + this.start + "\npriority: " + priority
    );
  }
}
