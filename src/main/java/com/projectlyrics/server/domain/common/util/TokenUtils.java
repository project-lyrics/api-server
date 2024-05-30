package com.projectlyrics.server.domain.common.util;

public class TokenUtils {

  private static final String TOKEN_PREFIX = "Bearer ";

  public static String extractToken(String header) {
    return header.replace(TOKEN_PREFIX, "");
  }
}
