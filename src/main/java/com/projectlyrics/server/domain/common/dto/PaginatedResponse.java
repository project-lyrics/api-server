package com.projectlyrics.server.domain.common.dto;

public record PaginatedResponse<T>(
    long totalPage,
    long totalSize,
    long itemSize,
    long currentPage,
    T data
) {
}
