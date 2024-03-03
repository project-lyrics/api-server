package com.projectlyrics.server.domain.common.dto;

public record ErrorResponse(
    String errorCode,
    String errorMessage
) {
}
