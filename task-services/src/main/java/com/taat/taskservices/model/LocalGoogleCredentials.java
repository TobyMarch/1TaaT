package com.taat.taskservices.model;

import java.io.Serializable;

public class LocalGoogleCredentials implements Serializable {
  public String type;
  public String client_id;
  public String client_secret;
  public String refresh_token;

  public LocalGoogleCredentials(String type, String client_id, String client_secret, String refresh_token) {
    this.type = type;
    this.client_id = client_id;
    this.client_secret = client_secret;
    this.refresh_token = refresh_token;
  }

  public String getType() {
    return type;
  }

  public String getClient_id() {
    return client_id;
  }

  public String getClient_secret() {
    return client_secret;
  }

  public String getRefresh_token() {
    return refresh_token;
  }
}
