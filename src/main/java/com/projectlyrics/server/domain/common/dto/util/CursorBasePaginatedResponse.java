package com.projectlyrics.server.domain.common.dto.util;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Slice;

public record CursorBasePaginatedResponse<T>(
    @Schema(description = "다음 페이지를 요청하기 위한 커서 값") boolean hasNext,
    @Schema(description = "요청한 데이터") List<T> data
) {

  public static <T> CursorBasePaginatedResponse<T> of(Slice<T> slice) {
    return new CursorBasePaginatedResponse<>(
        slice.hasNext(),
        slice.getContent()
    );
  }
}
