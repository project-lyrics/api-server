package com.projectlyrics.server.domain.auth.jwt.dto;

import com.projectlyrics.server.domain.auth.jwt.JwtValidationType;
import io.jsonwebtoken.Claims;

public record TokenValidationResult(
    JwtValidationType jwtValidationType,
    Claims claims
) {

  public static TokenValidationResult of(JwtValidationType jwtValidationType, Claims claims) {
    return new TokenValidationResult(jwtValidationType, claims);
  }

  public static TokenValidationResult of(JwtValidationType jwtValidationType) {
    return new TokenValidationResult(jwtValidationType, null);
  }
}
