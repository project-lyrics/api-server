package com.projectlyrics.server.domain.common.dto;

public record CursorBasePaginatedResponse<T>(
    String nextCursor,
    String currentCursor,
    String totalSize,
    String size,
    T data
) {
}
