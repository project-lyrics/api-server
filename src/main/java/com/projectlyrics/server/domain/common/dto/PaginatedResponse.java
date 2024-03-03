package com.projectlyrics.server.domain.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaginatedResponse<T>(
    @Schema(description = "총 페이지 수") long totalPage,
    @Schema(description = "데이터의 총 수") long totalSize,
    @Schema(description = "응답에 포함된 데이터의 수") long itemSize,
    @Schema(description = "현재 페이지 번호") long currentPage,
    @Schema(description = "요청한 데이터") T data
) {
}
