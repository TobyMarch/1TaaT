package com.taat.taskservices.services;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CalendarService {
  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String clientSecret;

  public String getAccessToken(String principalName) {
    OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(principalName);
    return authorizedClient.getAccessToken().getTokenValue();
  }

  public String refreshAccessToken(String principalName) {
    OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(principalName);
    String refreshToken = authorizedClient.getRefreshToken().getTokenValue();

    try {
      TokenResponse response = new GoogleRefreshTokenRequest(
              new NetHttpTransport(),
              new GsonFactory(),
              refreshToken,
              this.clientId,
              this.clientSecret
      ).execute();
      return response.getAccessToken();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  private OAuth2AuthorizedClient getAuthorizedClient(String principalName) {
    return authorizedClientService.loadAuthorizedClient(
            "google",
            principalName
    );
  }
}

