package com.projectlyrics.server.domain.common.dto.util;

import org.springframework.data.domain.Slice;

import java.util.List;

public record OffsetBasePaginatedResponse<T>(
        int pageNumber,
        boolean hasNext,
        List<T> data
) {

    public static <T> OffsetBasePaginatedResponse<T> of(Slice<T> slice) {
        return new OffsetBasePaginatedResponse<>(
                slice.getNumber(),
                slice.hasNext(),
                slice.getContent()
        );
    }

    public static <T> OffsetBasePaginatedResponse<T> of(int pageNumber, int pageSize, List<T> data) {
        if (data.size() > pageSize) {
            return new OffsetBasePaginatedResponse<>(pageNumber, true, data);
        }

        return new OffsetBasePaginatedResponse<>(pageNumber, false, data);
    }

    public  static  <T> OffsetBasePaginatedResponse<T> of(int pageNumber, boolean hasNext, List<T> data) {
        return new OffsetBasePaginatedResponse<>(pageNumber, hasNext, data);
    }
}
