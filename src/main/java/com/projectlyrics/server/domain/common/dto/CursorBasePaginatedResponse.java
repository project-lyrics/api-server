package com.projectlyrics.server.domain.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CursorBasePaginatedResponse<T>(
    @Schema(description = "다음 페이지를 요청하기 위한 커서 값") String nextCursor,
    @Schema(description = "현재 요청의 커서 값") String currentCursor,
    @Schema(description = "데이터의 총 수") long totalSize,
    @Schema(description = "응답에 포함된 데이터의 수") long itemSize,
    @Schema(description = "요청한 데이터") T data
) {
}
