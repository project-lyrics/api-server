package com.projectlyrics.server.domain.common.dto.util;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import org.springframework.data.domain.Slice;

public record CursorBasePaginatedResponse<T>(
        @Schema(description = "다음 페이지를 요청하기 위한 커서 값") Long cursor,
        @Schema(description = "다음 페이지 존재 여부") boolean hasNext,
        @Schema(description = "요청한 데이터") List<T> data
) {

    public static <T extends CursorResponse> CursorBasePaginatedResponse<T> of(Slice<T> slice) {
        return new CursorBasePaginatedResponse<>(
                slice.getContent().isEmpty() ? null : slice.getContent().getLast().getId(),
                slice.hasNext(),
                slice.getContent()
        );
    }
}
