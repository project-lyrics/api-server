package com.projectlyrics.server.global.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  @Profile("local")
  public SecurityFilterChain localHttpSecurity(HttpSecurity http) throws Exception {
    return http.csrf(CsrfConfigurer::disable)
        .formLogin(FormLoginConfigurer::disable)
        .authorizeHttpRequests(request -> request.anyRequest().permitAll())
        .build();
  }

  @Bean
  @Profile("stage")
  public SecurityFilterChain stageHttpSecurity(HttpSecurity http) throws Exception {
    return http.csrf(CsrfConfigurer::disable)
        .formLogin(FormLoginConfigurer::disable)
        .authorizeHttpRequests(request -> request.anyRequest().permitAll())
        .build();
  }

  @Bean
  @Profile("prod")
  public SecurityFilterChain prodHttpSecurity(HttpSecurity http) throws Exception {
    return http.csrf(CsrfConfigurer::disable)
        .formLogin(FormLoginConfigurer::disable)
        .authorizeHttpRequests(request -> request.anyRequest().authenticated())
        .build();
  }
}
