package com.projectlyrics.server.domain.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.user.dto.response.UserGetResponse;

import java.time.LocalDateTime;

public record CommentGetResponse(
        Long id,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,
        UserGetResponse writer
) {

    public static CommentGetResponse from(Comment comment) {
        return new CommentGetResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                UserGetResponse.from(comment.getWriter())
        );
    }

    public static CommentGetResponse from(Comment comment, LocalDateTime createdAt) {
        return new CommentGetResponse(
                comment.getId(),
                comment.getContent(),
                createdAt,
                UserGetResponse.from(comment.getWriter())
        );
    }
}
