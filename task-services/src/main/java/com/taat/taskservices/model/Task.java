package com.taat.taskservices.model;

import com.google.api.client.util.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document("tasksTest")
public class Task {

//  private String summary;
//  String start;
//  private int priority;
//  private String calendarId;
  private String owner;
  private String task;
  private String dueDate;
  private String addedDate;
  private int sliderValue;

//  public Task(String summary, String start, int priority, String calendarId) {
//    this.summary = summary;
//    this.start = start;
//    this.priority = priority;
//    this.calendarId = calendarId;
//  }


  public Task(String owner, String task, String dueDate, String addedDate, int sliderValue) {
    this.owner = owner;
    this.task = task;
    this.dueDate = dueDate;
    this.addedDate = addedDate;
    this.sliderValue = sliderValue;
  }

//  public String getSummary() {
//    return summary;
//  }
//
//  public String getStart() {
//    return start;
//  }
//
//  public int getPriority() {
//    return priority;
//  }
//
//  public String getCalendarId() {
//    return calendarId;
//  }


  public String getOwner() {
    return owner;
  }

  public String getTask() {
    return task;
  }

  public String getDueDate() {
    return dueDate;
  }

  public String getAddedDate() {
    return addedDate;
  }

  public int getSliderValue() {
    return sliderValue;
  }
}
