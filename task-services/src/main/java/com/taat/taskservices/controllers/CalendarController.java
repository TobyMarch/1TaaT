package com.taat.taskservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.auth.http.HttpCredentialsAdapter;
import com.taat.taskservices.TaskServicesApplication;
import com.taat.taskservices.model.LocalGoogleCredentials;
import com.taat.taskservices.model.Task;
import com.taat.taskservices.model.User;
import com.taat.taskservices.repository.UserRepository;
import com.taat.taskservices.services.CalendarService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.publisher.Mono;

import javax.imageio.IIOException;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CalendarController {

  @Autowired
  UserRepository userRepository;

  @Autowired
  CalendarService calendarService;

  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  private static final String APPLICATION_NAME = "1TaaT";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  @GetMapping("/calendar/oauth2/code/google")
  public RedirectView calendarRedirect(@RequestParam(required = false) String code) {
    if (code != null) {
      try {
        GoogleTokenResponse response = calendarService.requestAccessToken(code);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return new RedirectView("http://localhost:3000");
  }

  @GetMapping("/calendar/events")
  public ResponseEntity<List<Task>> getCalendarEvents() {
    List<Task> tasks = new ArrayList<>();

    try {
      Calendar calendar = calendarService.getCalendar();
      List<Event> events = calendarService.getEvents(calendar, 10);
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
//          tasks.add(new Task(event.getSummary(), start.toString(), 0, event.getId()));
          // tasks.add(new Task(event.getSummary(), OffsetDateTime.parse(start.toString(),
          // formatter), 0, event.getId()));
        }
      }
    } catch (IOException | GeneralSecurityException e) {
      e.printStackTrace();
    }

    return new ResponseEntity<>(tasks, HttpStatus.OK);
  }

  @GetMapping("/calendarSaved")
  public ResponseEntity<List<Task>> calendarSaved(@RequestParam String code, @RequestParam String userId) {
    List<Task> tasks = new ArrayList<>();
    try {
      GoogleTokenResponse response = calendarService.requestAccessToken(code);
      String refreshToken = response.getRefreshToken();
      NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

      Dotenv dotenv = Dotenv.load();
      LocalGoogleCredentials localCredentials = new LocalGoogleCredentials(
              dotenv.get("GOOGLE_CALENDAR_CREDENTIAL_TYPE"),
              dotenv.get("GOOGLE_CALENDAR_CLIENT_ID"),
              dotenv.get("GOOGLE_CALENDAR_CLIENT_SECRET"),
              refreshToken
      );

      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(localCredentials);
      InputStream in = new ByteArrayInputStream(json.getBytes());

      GoogleCredentials googleCredentials = GoogleCredentials.fromStream(in);
      HttpRequestInitializer credentials = new HttpCredentialsAdapter(googleCredentials);
      Calendar calendar = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
              .setApplicationName(APPLICATION_NAME)
              .build();

      List<Event> events = calendarService.getEvents(calendar, 10);
      tasks = buildList(events, tasks);

      userRepository.findById(userId)
              .flatMap(user -> {
                user.setCalendarRefreshToken(refreshToken);
                return userRepository.save(user);
              })
              .subscribe();

      return new ResponseEntity<>(tasks, HttpStatus.OK);
    } catch (IOException | GeneralSecurityException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
  }

  @GetMapping("/calendar")
  public ResponseEntity<Mono<List<Task>>> getCalendar(@RequestParam String userId) {
    Mono<List<Task>> tasks = userRepository.findById(userId)
            .map(user -> user.getCalendarRefreshToken())
            .map(token -> {
              List<Task> taskList = new ArrayList<>();
              try {
                NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

                Dotenv dotenv = Dotenv.load();
                LocalGoogleCredentials localCredentials = new LocalGoogleCredentials(
                        dotenv.get("GOOGLE_CALENDAR_CREDENTIAL_TYPE"),
                        dotenv.get("GOOGLE_CALENDAR_CLIENT_ID"),
                        dotenv.get("GOOGLE_CALENDAR_CLIENT_SECRET"),
                        token
                );

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(localCredentials);
                InputStream in = new ByteArrayInputStream(json.getBytes());

                GoogleCredentials googleCredentials = GoogleCredentials.fromStream(in);
                HttpRequestInitializer credentials = new HttpCredentialsAdapter(googleCredentials);
                Calendar calendar = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
                        .setApplicationName(APPLICATION_NAME)
                        .build();

                List<Event> events = calendarService.getEvents(calendar, 10);
                taskList = buildList(events, taskList);
              } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
              }
              return taskList;
            });
    return new ResponseEntity<>(tasks, HttpStatus.OK);
  }

  private List<Task> buildList(List<Event> events, List<Task> tasks) {
    for (Event event : events) {
      DateTime start = event.getStart().getDateTime();
      if (start == null) {
        start = event.getStart().getDate();
      }
      System.out.println(event.getSummary());
//      tasks.add(new Task(event.getSummary(), start.toString(), 0, event.getId()));
    }
    return tasks;
  }

  @GetMapping("/calendar/checkRefreshToken")
  public ResponseEntity<?> checkRefreshToken(@AuthenticationPrincipal OAuth2User principal) {
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
            "google",
            principal.getName()
    );

    System.out.println(authorizedClient.getAccessToken().getTokenValue());
    System.out.println(authorizedClient.getRefreshToken().getTokenValue());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @GetMapping("/calendar/getAccessToken")
  public ResponseEntity<?> getAccessToken(@AuthenticationPrincipal OAuth2User principal) {
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
            "google",
            principal.getName()
    );

    System.out.println(authorizedClient.getAccessToken().getTokenValue());
    System.out.println(authorizedClient.getRefreshToken().getTokenValue());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }
}
