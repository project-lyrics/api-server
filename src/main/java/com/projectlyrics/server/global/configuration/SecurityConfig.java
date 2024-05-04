package com.projectlyrics.server.global.configuration;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.projectlyrics.server.domain.auth.authentication.JwtAuthenticationEntryPoint;
import com.projectlyrics.server.domain.auth.authentication.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  @Profile("local")
  public SecurityFilterChain localHttpSecurity(HttpSecurity http) throws Exception {
    permitDevelopApis(http);
    setHttp(http);

    return http.build();
  }

  @Bean
  @Profile("stage")
  public SecurityFilterChain stageHttpSecurity(HttpSecurity http) throws Exception {
    permitDevelopApis(http);
    setHttp(http);

    return http.build();
  }

  @Bean
  @Profile("prod")
  public SecurityFilterChain prodHttpSecurity(HttpSecurity http) throws Exception {
    setHttp(http);

    return http.build();
  }

  private void permitDevelopApis(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
            .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/docs/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/actuator/health")).permitAll());
  }

  // TODO : 비회원 허용 API를 추가적으로 등록해야 함
  private void setHttp(HttpSecurity http) throws Exception {
    http
        .csrf(CsrfConfigurer::disable)
        .formLogin(FormLoginConfigurer::disable)
        .httpBasic(HttpBasicConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .exceptionHandling(exception ->
            exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .authorizeHttpRequests(requests ->
            requests
                .requestMatchers(new AntPathRequestMatcher("/api/v1/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v1/artists/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/v1/test/**")).permitAll()
                .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
