package com.taat.taskservices.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usersTest")
public class User {

  @Id
  private String userId;
  private String calendarRefreshToken;

  public User(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getCalendarRefreshToken() {
    return calendarRefreshToken;
  }

  public void setCalendarRefreshToken(String calendarRefreshToken) {
    this.calendarRefreshToken = calendarRefreshToken;
  }
}
