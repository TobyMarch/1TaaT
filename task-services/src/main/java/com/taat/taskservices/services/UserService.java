package com.taat.taskservices.services;

import com.taat.taskservices.model.User;
import com.taat.taskservices.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService extends OidcUserService {

  @Autowired
  UserRepository userRepository;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = super.loadUser(userRequest);
    userRepository.save(new User(oidcUser.getName(), oidcUser.getAttribute("email")));
    return oidcUser;
  }

  public User getOrAddUser(Map<String, Object> userDetails) {
    String userId = userDetails.get("sub").toString();
    Optional<User> user = userRepository.findById(userId);
    return user.orElse(userRepository.save(new User(userId, userDetails.get("email").toString())));
  }
}
