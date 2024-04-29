package com.projectlyrics.server.domain.auth.authentication;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectlyrics.server.domain.common.dto.ErrorResponse;
import com.projectlyrics.server.global.message.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomJwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_TOKEN);

    response.setCharacterEncoding("UTF-8");
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
