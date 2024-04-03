package com.projectlyrics.server.domain.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record CommonResponse<T>(
    @Schema(description = "요청의 성공 여부") boolean success,
    @Schema(description = "http status code") HttpStatus responseStatus,
    @Schema(description = "Response body") T data
) {
}
