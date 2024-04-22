package com.taat.taskservices.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins(
            "http://localhost:3000",
            "https://onetaat-web.onrender.com",
            "https://onetaat.com"
    ).allowCredentials(true);
  }
}
