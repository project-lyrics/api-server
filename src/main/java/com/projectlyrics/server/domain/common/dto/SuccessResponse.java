package com.projectlyrics.server.domain.common.dto;

import com.projectlyrics.server.domain.common.message.SuccessMessage;

public record SuccessResponse<T>(
    String message,
    T data
) {
  public static <T> SuccessResponse<T> of(SuccessMessage message, T data) {
    return new SuccessResponse<>(message.getMessage(), data);
  }

  public static SuccessResponse<Void> of(SuccessMessage message) {
    return new SuccessResponse<>(message.getMessage(), null);
  }
}
