package com.taat.taskservices.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("usersTest")
public class User {

  @Id
  private String userId;
  private String userEmail;
  private String calendarRefreshToken;

  public User(String userId, String userEmail) {
    this.userId = userId;
    this.userEmail = userEmail;
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
