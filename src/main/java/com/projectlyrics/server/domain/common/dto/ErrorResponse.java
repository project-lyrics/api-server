package com.projectlyrics.server.domain.common.dto;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "에러 코드") String errorCode,
        @Schema(description = "에러 메시지") String errorMessage,
        @Schema(description = "추가 데이터", nullable = true) Object data
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage(), null);
    }

    public static ErrorResponse of(ErrorCode errorCode, String errorMessage) {
        return new ErrorResponse(errorCode.getErrorCode(), errorMessage, null);
    }
    public static ErrorResponse of(ErrorCode errorCode, Object data) {
        return new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage(), data);
    }

    public static ErrorResponse of(ErrorCode errorCode, String errorMessage, Object data) {
        return new ErrorResponse(errorCode.getErrorCode(), errorMessage, data);
    }
}

