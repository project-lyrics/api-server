package com.projectlyrics.server.domain.comment.dto.request;

public record CommentCreateRequest(
        String content,
        Long noteId
) {
}
