package com.projectlyrics.server.domain.common.dto;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "에러 코드") String errorCode,
        @Schema(description = "에러 메시지") String errorMessage
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode, String errorMessage) {
        return new ErrorResponse(errorCode.getErrorCode(), errorMessage);
    }
}
