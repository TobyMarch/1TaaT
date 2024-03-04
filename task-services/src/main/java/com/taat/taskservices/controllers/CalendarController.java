package com.taat.taskservices.controllers;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.taat.taskservices.model.Task;
import com.taat.taskservices.services.CalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CalendarController {

  @CrossOrigin(origins = { "http://localhost:3000", "https://onetaat-web.onrender.com" })
  @GetMapping("/calendar/events")
  public ResponseEntity<List<Task>> getCalendarEvents() {
    List<Task> tasks = new ArrayList<>();

    try {
      Calendar calendar = CalendarService.getCalendar();
      List<Event> events = CalendarService.getEvents(calendar, 10);
      if (events.isEmpty()) {
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
      } else {
        for (Event event : events) {
          DateTime start = event.getStart().getDateTime();
          // DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
          if (start == null) {
            start = event.getStart().getDate();
            // formatter = DateTimeFormatter.ISO_LOCAL_DATE;
          }
          tasks.add(new Task(event.getSummary(), start.toString(), 0, event.getId()));
          // tasks.add(new Task(event.getSummary(), OffsetDateTime.parse(start.toString(),
          // formatter), 0, event.getId()));
        }
      }
    } catch (IOException | GeneralSecurityException e) {
      e.printStackTrace();
    }

    return new ResponseEntity<>(tasks, HttpStatus.OK);
  }
}
