package com.projectlyrics.server.domain.like.dto.response;

public record LikesCountResponse(
        long likesCount
) {

    public static LikesCountResponse of(long likesCount) {
        return new LikesCountResponse(likesCount);
    }
}
