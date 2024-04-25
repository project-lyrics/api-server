package com.projectlyrics.server.domain.common.dto;

public record SuccessResponse<T>(
        int status,
        T data
) {
    public static <T> SuccessResponse of(int status, T data) {
        return new SuccessResponse(status, data);
    }

    public static SuccessResponse of(int status) {
        return new SuccessResponse(status, null);
    }
}
