package com.projectlyrics.server.domain.comment.domain;

import com.projectlyrics.server.domain.comment.dto.request.CommentUpdateRequest;

public record CommentUpdate(
        String content
) {

    public static CommentUpdate from(CommentUpdateRequest request) {
        return new CommentUpdate(request.content());
    }
}
