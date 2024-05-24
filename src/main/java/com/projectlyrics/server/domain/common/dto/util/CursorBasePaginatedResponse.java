package com.projectlyrics.server.domain.common.dto.util;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Slice;

public record CursorBasePaginatedResponse<T>(
    @Schema(description = "다음 페이지를 요청하기 위한 커서 값") String nextCursor,
    @Schema(description = "현재 요청의 커서 값") String currentCursor,
    @Schema(description = "데이터의 총 수") long totalSize,
    @Schema(description = "응답에 포함된 데이터의 수") long itemSize,
    @Schema(description = "요청한 데이터") List<T> data
) {

  public static <T> CursorBasePaginatedResponse<T> of(Slice<T> slice, Long nextCursor, Long cursor) {
    return new CursorBasePaginatedResponse<>(
        slice.hasNext() ? String.valueOf(nextCursor) : null,
        cursor != null ? String.valueOf(cursor) : String.valueOf(0),
        slice.getNumberOfElements(),
        slice.getSize(),
        slice.getContent()
    );
  }
}
