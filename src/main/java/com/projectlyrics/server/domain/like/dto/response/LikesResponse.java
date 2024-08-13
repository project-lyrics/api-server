package com.projectlyrics.server.domain.like.dto.response;

public record LikesResponse(
        long likesCount,
        Long noteId
) {

    public static LikesResponse of(long likesCount, Long noteId) {
        return new LikesResponse(likesCount, noteId);
    }
}
