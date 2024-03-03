package com.projectlyrics.server.domain.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
    @Schema(description = "에러 코드") String errorCode,
    @Schema(description = "에러 메시지") String errorMessage
) {
}
